package assignment10.repositories;

import java.sql.Connection;
import java.sql.SQLException;

import assignment10.RecordNotFoundException;
import assignment10.dtos.DTOBase;

public interface IRepository<TDTO extends DTOBase> {

	Connection getConnection();

	void add(TDTO dto) throws SQLException;

	void update(TDTO dto) throws SQLException;
	
	void addOrUpdate(TDTO dto) throws SQLException;

	void delete(TDTO dto) throws SQLException;

	TDTO findById(int id) throws SQLException, RecordNotFoundException;

	void beginTransaction() throws SQLException;

	void commitTransaction() throws SQLException;

	void rollbackTransaction() throws SQLException;
	
	int getCount() throws SQLException;
	
	boolean exists(TDTO dto) throws Exception;
}