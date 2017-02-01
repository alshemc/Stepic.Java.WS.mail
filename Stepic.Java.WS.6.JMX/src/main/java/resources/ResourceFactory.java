package resources;

import sax.ReadXMLFileSAX;

/**
 * @author Alesha
 */
public class ResourceFactory {
    private static ResourceFactory instance;

    private ResourceFactory() {
    }

    public static ResourceFactory instance() {
        if (instance == null) {
            instance = new ResourceFactory();
        }
        return instance;
    }

    public <T> T getResource(String path, Class<T> clazz) {
        return clazz.cast(ReadXMLFileSAX.readXML(path));
    }
}
