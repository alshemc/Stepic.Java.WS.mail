package base;

import dbService.DBException;

/**
 * @author Alesha
 */
public interface DBService {
    long addUser(UserProfile userProfile) throws DBException;

    UserProfile getUser(String login) throws DBException;

    void create() throws DBException;

    void cleanUp() throws DBException;

    void check() throws DBException;
}
