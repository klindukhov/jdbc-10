package assignment10.test;

import assignment10.dtos.DTOBase;
import assignment10.repositories.IRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;

public abstract class RepositoryTestBase<TDTO extends DTOBase, TRepository extends IRepository<TDTO>> {

	private TRepository _repository;

	protected TRepository getRepository(){
	    return this._repository;
    }



	@BeforeEach
	public void before() throws SQLException {
		_repository = Create();
		if (_repository != null) {
			_repository.beginTransaction();
		}
	}

	@AfterEach
	public void after() throws SQLException {
		if (_repository != null) {
			_repository.rollbackTransaction();
		}
	}

	protected abstract TRepository Create();
}