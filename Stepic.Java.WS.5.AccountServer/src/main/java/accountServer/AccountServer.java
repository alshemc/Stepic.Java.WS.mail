package accountServer;

/**
 * @author Alesha
 * 
 * This is example to check capabilities of the testing application
 * 
 * Realization Interface of the account server
 */
public class AccountServer implements AccountServerI {
    private int usersCount;
    private int usersLimit;

    public AccountServer(int usersLimit) {
        this.usersCount = 0;
        this.usersLimit = usersLimit;
    }

    @Override
    public void addNewUser() {
        usersCount += 1;
    }

    @Override
    public void removeUser() {
        if (usersCount > 0)
            usersCount -= 1;
    }

    @Override
    public int getUsersLimit() {
        return usersLimit;
    }

    @Override
    public void setUsersLimit(int usersLimit) {
        this.usersLimit = usersLimit;
    }

    @Override
    public int getUsersCount() {
        return usersCount;
    }
}
