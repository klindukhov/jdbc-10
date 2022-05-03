package assignment10.test;

import assignment10.RecordNotFoundException;
import assignment10.dtos.GroupDTO;
import assignment10.dtos.UserDTO;
import assignment10.repositories.GroupRepository;
import assignment10.repositories.IUserRepository;
import assignment10.repositories.UserRepository;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public final class UserRepositoryTest extends RepositoryTestBase<UserDTO, IUserRepository> {

	@Test
	public void add() throws Exception {
		UserDTO dto = new UserDTO(1, "userlog", "userpass");
        GroupDTO group = new GroupDTO(0, "group1", "desc1");
        dto.addGroup(group);
        new GroupRepository().add(group);

	    getRepository().add(dto);
	    UserDTO byId = getRepository().findById(dto.getId());
        getRepository().loadGroups(byId);

		assertEquals(1, getRepository().getCount());
        assertEquals("group1", byId.getGroups().get(0).getName());
	}

	@Test
	public void loadGroups() throws SQLException, RecordNotFoundException {
        UserDTO dto = new UserDTO(0, "userLogin", "userPass");
        GroupDTO group = new GroupDTO(0, "group1", "desc1");
        dto.addGroup(group);
        GroupDTO group1 = new GroupDTO(0, "group2", "desc2");
        dto.addGroup(group1);

        new GroupRepository().add(group);
        new GroupRepository().add(group1);
        getRepository().add(dto);

        UserDTO byId = getRepository().findById(dto.getId());
        getRepository().loadGroups(byId);

        assertEquals(2, byId.getGroups().size());
        assertEquals("group1", byId.getGroups().get(0).getName());
        assertEquals("group2", byId.getGroups().get(1).getName());
    }

	@Test
	public void update() throws SQLException, RecordNotFoundException {
        UserDTO dto = new UserDTO(1,"user1","pass1");
        GroupDTO group1 = new GroupDTO(0, "groupName", "descript");

        dto.addGroup(group1);
        new GroupRepository().add(group1);
        getRepository().add(dto);

        dto.setLogin("foo");
        GroupDTO group2 = new GroupDTO(0,"group2","desc2");
        dto.addGroup(group2);
        new GroupRepository().add(group2);
        getRepository().update(dto);

        UserDTO byId = getRepository().findById(1);
        getRepository().loadGroups(byId);

        assertEquals(dto.getId(), byId.getId());
        assertEquals(dto.getLogin(), byId.getLogin());
        assertEquals(dto.getPassword(), byId.getPassword());
        assertEquals("groupName",byId.getGroups().get(0).getName());
        assertEquals("group2",byId.getGroups().get(1).getName());
	}

	@Test
	public void addOrUpdate() throws SQLException, RecordNotFoundException {
	    UserDTO dto = new UserDTO(1,"user1","pass1");
        GroupDTO group1 = new GroupDTO(0, "groupName", "descript");

        dto.addGroup(group1);
        new GroupRepository().add(group1);
        getRepository().addOrUpdate(dto);

        dto.setLogin("foo");
        GroupDTO group2 = new GroupDTO(0,"group2","desc2");
        dto.addGroup(group2);
        new GroupRepository().add(group2);
        getRepository().addOrUpdate(dto);

        UserDTO byId = getRepository().findById(1);
        getRepository().loadGroups(byId);

        assertEquals(dto.getId(), byId.getId());
        assertEquals(dto.getLogin(), byId.getLogin());
        assertEquals(dto.getPassword(), byId.getPassword());
        assertEquals("groupName",byId.getGroups().get(0).getName());
        assertEquals("group2",byId.getGroups().get(1).getName());
	}

	@Test
	public void delete() throws SQLException {
	    UserDTO userDTO = new UserDTO(0, "foo", "userpass");
        getRepository().add(userDTO);
        System.out.println();
        getRepository().delete(userDTO);

        assertEquals(0, getRepository().getCount());
	}

	@Test
	public void findById() throws SQLException, RecordNotFoundException {
        UserDTO dto = new UserDTO(1, "userlog", "userpass");
        getRepository().add(dto);
        UserDTO byId = getRepository().findById(1);

        assertEquals(dto.getId(), byId.getId());
        assertEquals(dto.getLogin(), byId.getLogin());
        assertEquals(dto.getPassword(), byId.getPassword());
	}

	@Test
    public void count() throws SQLException {
        getRepository().add(new UserDTO(0, "userlog", "userpass"));
        getRepository().add(new UserDTO(0, "foo", "userpass"));

        assertEquals(2, getRepository().getCount());
    }
	
	@Test
	public void findByName() throws SQLException {
        getRepository().add(new UserDTO(0, "userlog", "userpass"));
        getRepository().add(new UserDTO(0, "foo", "userpass"));

        List<UserDTO> byName = getRepository().findByName("%log");
        assertEquals(1, byName.size());
        assertEquals("userlog", byName.get(0).getLogin());
        assertEquals("userpass", byName.get(0).getPassword());
	}
	
	@Override
	protected IUserRepository Create() {
	    return new UserRepository();
	}
}