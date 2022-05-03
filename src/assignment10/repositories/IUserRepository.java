package assignment10.repositories;

import java.sql.SQLException;
import java.util.List;

import assignment10.dtos.GroupDTO;
import assignment10.dtos.UserDTO;

public interface IUserRepository extends IRepository<UserDTO> {

    List<UserDTO> findByName(String username) throws SQLException;

    void loadGroups(UserDTO dto) throws SQLException;
}