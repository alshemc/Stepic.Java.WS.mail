package accountServer;

/**
 * @author Alesha
 */
public interface ResourceServer {
    String getName();

    int getAge();

    void readResource(String path);
}
