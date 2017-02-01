package accountServer;

/**
 * @author Alesha
 * 
 * This is example to check capabilities of the testing application
 * 
 * Interface for counting server account
 */
public interface AccountServerI {
    void addNewUser();

    void removeUser();

    int getUsersLimit();

    void setUsersLimit(int usersLimit);

    int getUsersCount();
}
