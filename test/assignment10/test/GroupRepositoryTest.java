package assignment10.test;


import assignment10.RecordNotFoundException;
import assignment10.dtos.GroupDTO;
import assignment10.dtos.UserDTO;
import assignment10.repositories.GroupRepository;
import assignment10.repositories.IGroupRepository;
import assignment10.repositories.UserRepository;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupRepositoryTest extends RepositoryTestBase<GroupDTO, IGroupRepository> {

	@Test
	public void add() throws SQLException, RecordNotFoundException {
        GroupDTO dto = new GroupDTO(1, "groupName", "descript");
        UserDTO user = new UserDTO(0, "user1", "asdlasda");
        dto.addUser(user);
        new UserRepository().add(user);

        getRepository().add(dto);

        GroupDTO byId = getRepository().findById(dto.getId());
        getRepository().loadUsers(byId);

        assertEquals(1, getRepository().getCount());
        assertEquals("user1", byId.getUsers().get(0).getLogin());
	}

	@Test
    public void loadUsers() throws SQLException, RecordNotFoundException {
        GroupDTO dto = new GroupDTO(0, "groupName", "descript");
        UserDTO user = new UserDTO(0, "user1", "asdlasda");
        dto.addUser(user);
        UserDTO user1 = new UserDTO(0, "user2", "asdlasda");
        dto.addUser(user1);

        new UserRepository().add(user);
        new UserRepository().add(user1);
        getRepository().add(dto);

        GroupDTO byId = getRepository().findById(dto.getId());
        getRepository().loadUsers(byId);

        assertEquals(2, byId.getUsers().size());
        assertEquals("user1", byId.getUsers().get(0).getLogin());
        assertEquals("user2", byId.getUsers().get(1).getLogin());
    }


	@Test
	public void update() throws SQLException, RecordNotFoundException {
        GroupDTO dto = new GroupDTO(1, "groupName", "descript");
        UserDTO user1 = new UserDTO(0,"user1","pass1");
        dto.addUser(user1);
        new UserRepository().add(user1);
        getRepository().add(dto);

        dto.setName("foo");
        UserDTO user2 = new UserDTO(0,"user2","pass1");
        dto.addUser(user2);
        new UserRepository().add(user2);
        getRepository().update(dto);

        GroupDTO byId = getRepository().findById(1);
        getRepository().loadUsers(byId);

        assertEquals(dto.getId(), byId.getId());
        assertEquals(dto.getName(), byId.getName());
        assertEquals(dto.getDescription(), byId.getDescription());
        assertEquals("user1",byId.getUsers().get(0).getLogin());
        assertEquals("user2",byId.getUsers().get(1).getLogin());
    }

	@Test
	public void addOrUpdate() throws SQLException, RecordNotFoundException {
        GroupDTO dto = new GroupDTO(1, "groupName", "descript");
        UserDTO user1 = new UserDTO(0,"user1","pass1");
        dto.addUser(user1);
        new UserRepository().add(user1);
        getRepository().addOrUpdate(dto);

        dto.setName("foo");
        UserDTO user2 = new UserDTO(0,"user2","pass1");
        dto.addUser(user2);
        new UserRepository().add(user2);
        getRepository().addOrUpdate(dto);

        GroupDTO byId = getRepository().findById(1);
        getRepository().loadUsers(byId);

        assertEquals(dto.getId(), byId.getId());
        assertEquals(dto.getName(), byId.getName());
        assertEquals(dto.getDescription(), byId.getDescription());
        assertEquals("user1",byId.getUsers().get(0).getLogin());
        assertEquals("user2",byId.getUsers().get(1).getLogin());
	}

	@Test
	public void delete() throws SQLException {
        GroupDTO dto = new GroupDTO(1, "groupName", "descript");
        getRepository().add(dto);
        System.out.println();
        getRepository().delete(dto);

        assertEquals(0, getRepository().getCount());
	}

	@Test
	public void findById() throws SQLException, RecordNotFoundException {
        GroupDTO dto = new GroupDTO(1, "groupName", "descript");
        getRepository().add(dto);
        GroupDTO byId = getRepository().findById(1);

        assertEquals(dto.getId(), byId.getId());
        assertEquals(dto.getName(), byId.getName());
        assertEquals(dto.getDescription(), byId.getDescription());
	}

    @Test
    public void count() throws SQLException {
        getRepository().add(new GroupDTO(0, "grup", "one"));
        getRepository().add(new GroupDTO(0, "grup2", "anothergrup"));

        assertEquals(2, getRepository().getCount());
    }

	@Test
	public void findByName() throws SQLException {
		getRepository().add(new GroupDTO(0, "groupname", "groupDescription"));
		getRepository().add(new GroupDTO(0, "foo", "groupDes"));

		List<GroupDTO> byName = getRepository().findByName("%name");
		assertEquals(1, byName.size());
		assertEquals("groupname", byName.get(0).getName());
		assertEquals("groupDescription", byName.get(0).getDescription());
	}

	@Override
	protected IGroupRepository Create() {
        return new GroupRepository();
	}
}

