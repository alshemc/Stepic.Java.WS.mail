package accountServer;

/**
 * @author Alesha
 * 
 * This is example to check capabilities of the testing application
 * 
 * Interface throw manageable bean (Java Management eXtension) for control AccountServer 
 */
@SuppressWarnings("UnusedDeclaration")
public interface AccountServerControllerMBean {
    public int getUsers();

    public int getUsersLimit();

    public void setUsersLimit(int usersLimit);
}
