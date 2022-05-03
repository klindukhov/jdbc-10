package assignment10.repositories;

import java.sql.SQLException;
import java.util.List;

import assignment10.dtos.GroupDTO;

public interface IGroupRepository extends IRepository<GroupDTO> {

	List<GroupDTO> findByName(String name) throws SQLException;

	void loadUsers(GroupDTO dto) throws SQLException;
}