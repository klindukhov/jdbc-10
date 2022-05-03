package assignment10.repositories;

import assignment10.MyConnection;
import assignment10.RecordNotFoundException;
import assignment10.dtos.GroupDTO;
import assignment10.dtos.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupRepository implements IGroupRepository {

    private Connection connection = MyConnection.getConnection();

    @Override
    public List<GroupDTO> findByName(String name) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select * from groups where group_name like ?")) {
            statement.setString(1,  name);
            ResultSet resultSet = statement.executeQuery();
            List<GroupDTO> groups = new ArrayList<>();
            while(resultSet.next()) {
                int id1 = resultSet.getInt("group_id");
                String groupName = resultSet.getString("group_name");
                String groupDescription = resultSet.getString("group_description");
                groups.add(new GroupDTO(id1, groupName, groupDescription));
            }
            return groups;
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void add(GroupDTO dto) throws SQLException {
        if(dto.hasExistingId()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO groups (group_id, group_name, group_description) VALUES (?, ?, ?)")) {
                statement.setInt(1, dto.getId());
                statement.setString(2, dto.getName());
                statement.setString(3, dto.getDescription());
                statement.execute();
            }
        } else {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO groups ( group_name, group_description) VALUES ( ?, ?) RETURNING group_id")) {
                statement.setString(1, dto.getName());
                statement.setString(2, dto.getDescription());
                ResultSet resultset = statement.executeQuery();
                resultset.next();
                dto.setId(resultset.getInt("group_id"));
            }
        }
        addToAssoc(dto);

    }

    private void addToAssoc(GroupDTO dto) throws SQLException {
        if (dto.getUsers() == null || dto.getUsers().isEmpty()) {
            return;
        }

        String sql = "Insert into user_group (group_id, user_id) values " +
                dto.getUsers().stream().map(u -> "(?, ?)")
                        .collect(Collectors.joining(", "));
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            int statementParameter = 1;
            for(UserDTO user : dto.getUsers()) {
                statement.setInt(statementParameter++, dto.getId());
                statement.setInt(statementParameter++, user.getId());

            }
            statement.execute();
        }
    }

    private void deleteAssoc(GroupDTO dto) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement("delete from user_group where group_id = ? ")){
            statement.setInt(1,dto.getId());
            statement.execute();
        }


    }

    @Override
    public void update(GroupDTO dto) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE groups " +
                "SET group_name = ?, " +
                "    group_description = ? " +
                "WHERE " +
                "   group_id = ?")) {
            statement.setString(1, dto.getName());
            statement.setString(2, dto.getDescription());
            statement.setInt(3,dto.getId());
            statement.execute();
        }
        deleteAssoc(dto);
        addToAssoc(dto);
    }

    @Override
    public void addOrUpdate(GroupDTO dto) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO groups(group_id, group_name, group_description) VALUES(?, ?, ?) " +
                "ON CONFLICT (group_id) " +
                "DO UPDATE " +
                "SET group_name = ?, " +
                "    group_description = ?;")) {
            statement.setInt(1,dto.getId());
            statement.setString(2, dto.getName());
            statement.setString(3, dto.getDescription());
            statement.setString(4, dto.getName());
            statement.setString(5, dto.getDescription());
            statement.execute();
        }

        deleteAssoc(dto);
        addToAssoc(dto);

    }

    @Override
    public void loadUsers(GroupDTO dto) throws SQLException {
        try(PreparedStatement statement1 = connection.prepareStatement("select u.* from user_group join users u on user_group.user_id = u.user_id where group_id = ?")) {
        ArrayList<UserDTO> userDTOS = new ArrayList<>();
            statement1.setInt(1, dto.getId());
            ResultSet set = statement1.executeQuery();
            while(set.next()) {
                int user_id = set.getInt("user_id");
                String userLogin = set.getString("user_login");
                String userPassword = set.getString("user_password");
                userDTOS.add(new UserDTO(user_id, userLogin, userPassword));
            }
            dto.setUsers(userDTOS);
        }
    }

    @Override
    public void delete(GroupDTO dto) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("delete from groups where group_id = ?")){
            statement.setInt(1,dto.getId());
            statement.execute();
        }
    }

    @Override
    public GroupDTO findById(int id) throws SQLException, RecordNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement("select * from groups where group_id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                throw new RecordNotFoundException();
            }
            int id1 = resultSet.getInt("group_id");
            String groupName = resultSet.getString("group_name");
            String groupDescription = resultSet.getString("group_description");
            return new GroupDTO(id1, groupName, groupDescription);
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
            ResultSet resultSet = statement.executeQuery("select count(*) from groups");
            resultSet.next();
            return resultSet.getInt("count");
        }
    }

    @Override
    public boolean exists(GroupDTO dto) throws Exception {
        try(PreparedStatement statement = connection.prepareStatement(
                "select count(*) from groups where group_id = ?"
        )){
            statement.setInt(1, dto.getId());
            ResultSet resultSet = statement.executeQuery("select count(*) from groups");
            resultSet.next();
            return resultSet.getInt("count") > 0;
        }
    }
}
