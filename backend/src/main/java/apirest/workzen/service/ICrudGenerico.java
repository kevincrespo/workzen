package apirest.workzen.service;

import java.util.List;

public interface ICrudGenerico<J, ID> {
	J findOne(ID atributoPK);
	List<J> findAll();
	J insert(J objeto);
	J update(ID atributoPK, J objeto);
	boolean delete(ID atributoPK);
}
