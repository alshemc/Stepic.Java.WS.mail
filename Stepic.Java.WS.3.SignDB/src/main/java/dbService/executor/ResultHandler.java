package dbService.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alesha
 * 
 */
public interface ResultHandler<T> {
	T handle(ResultSet resultSet) throws SQLException;
}
