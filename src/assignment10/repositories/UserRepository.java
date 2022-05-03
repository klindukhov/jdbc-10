package assignment10.repositories;

import assignment10.MyConnection;
import assignment10.RecordNotFoundException;
import assignment10.dtos.GroupDTO;
import assignment10.dtos.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserRepository implements IUserRepository {

    private Connection connection = MyConnection.getConnection();
    @Override
    public List<UserDTO> findByName(String username) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select * from users where user_login like ?")) {
            statement.setString(1,  username);
            ResultSet resultSet = statement.executeQuery();
            List<UserDTO> users = new ArrayList<>();
            while(resultSet.next()) {
                int id1 = resultSet.getInt("user_id");
                String userLogin = resultSet.getString("user_login");
                String userPassword = resultSet.getString("user_password");
                users.add(new UserDTO(id1, userLogin, userPassword));
            }
            return users;
        }
    }


    private void addToAssoc(UserDTO dto) throws SQLException {
        if (dto.getGroups() == null || dto.getGroups().isEmpty()) {
            return;
        }

        String sql = "Insert into user_group (group_id, user_id) values " +
                dto.getGroups().stream().map(u -> "(?, ?)")
                        .collect(Collectors.joining(", "));
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            int statementParameter = 1;
            for(GroupDTO groupDTO : dto.getGroups()) {
                statement.setInt(statementParameter++, groupDTO.getId());
                statement.setInt(statementParameter++, dto.getId());

            }
            statement.execute();
        }
    }

    private void deleteAssoc(UserDTO dto) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement("delete from user_group where user_id = ? ")){
            statement.setInt(1,dto.getId());
            statement.execute();
        }


    }

    @Override
    public void loadGroups(UserDTO dto) throws SQLException {
        try(PreparedStatement statement1 = connection.prepareStatement("select g.* from user_group join groups g on user_group.group_id = g.group_id where user_id = ?")) {
            ArrayList<GroupDTO> groupDTOS = new ArrayList<>();
            statement1.setInt(1, dto.getId());
            ResultSet set = statement1.executeQuery();
            while(set.next()) {
                int id1 = set.getInt("group_id");
                String groupName = set.getString("group_name");
                String groupDescription = set.getString("group_description");
                groupDTOS.add(new GroupDTO(id1, groupName, groupDescription));
            }
            dto.setGroups(groupDTOS);
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void add(UserDTO dto) throws SQLException {
        if(dto.hasExistingId()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users (user_id, user_login, user_password) VALUES (?, ?, ?)")) {
                statement.setInt(1, dto.getId());
                statement.setString(2, dto.getLogin());
                statement.setString(3, dto.getPassword());
                statement.execute();
            }
        } else {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users (user_login, user_password) VALUES (?, ?) returning user_id")) {
                statement.setString(1, dto.getLogin());
                statement.setString(2, dto.getPassword());
                ResultSet resultset = statement.executeQuery();
                resultset.next();
                dto.setId(resultset.getInt("user_id"));
            }
        }
        addToAssoc(dto);
    }

    @Override
    public void update(UserDTO dto) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE users " +
                "SET user_login = ?, " +
                "    user_password = ? " +
                "WHERE " +
                "   user_id = ?")) {
            statement.setString(1, dto.getLogin());
            statement.setString(2, dto.getPassword());
            statement.setInt(3,dto.getId());
            statement.execute();
        }
        deleteAssoc(dto);
        addToAssoc(dto);

    }

    @Override
    public void addOrUpdate(UserDTO dto) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users(user_id, user_login, user_password) VALUES(?, ?, ?) " +
                "ON CONFLICT (user_id) " +
                "DO UPDATE " +
                "SET user_login = ?, " +
                "    user_password = ?;")) {
            statement.setInt(1,dto.getId());
            statement.setString(2, dto.getLogin());
            statement.setString(3, dto.getPassword());
            statement.setString(4, dto.getLogin());
            statement.setString(5, dto.getPassword());
            statement.execute();
        }
        deleteAssoc(dto);
        addToAssoc(dto);

    }

    @Override
    public void delete(UserDTO dto) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("delete from users where user_id = ?")){
            statement.setInt(1,dto.getId());
            statement.execute();
        }

    }

    @Override
    public UserDTO findById(int id) throws SQLException, RecordNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement("select * from users where user_id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                throw new RecordNotFoundException();
            }
            int id1 = resultSet.getInt("user_id");
            String userLogin = resultSet.getString("user_login");
            String userPassword = resultSet.getString("user_password");
            return new UserDTO(id1, userLogin, userPassword);
        }
    }

    @Override
    public void beginTransaction() throws SQLException {
        try(Statement statement = connection.createStatement()){
            statement.execute("begin transaction");
        }

    }

    @Override
    public void commitTransaction() throws SQLException {
        try(Statement statement = connection.createStatement()){
            statement.execute("commit transaction");
        }

    }

    @Override
    public void rollbackTransaction() throws SQLException {
        try(Statement statement = connection.createStatement()){
            statement.execute("rollback transaction");
        }

    }

    @Override
    public int getCount() throws SQLException {
        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("select count(*) from users");
            resultSet.next();
            return resultSet.getInt("count");
        }
    }

    @Override
    public boolean exists(UserDTO dto) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(
                "select count(*) from users where user_id = ?"
        )){
            statement.setInt(1, dto.getId());
            ResultSet resultSet = statement.executeQuery("select count(*) from users");
            resultSet.next();
            return resultSet.getInt("count") > 0;
        }
    }
}
