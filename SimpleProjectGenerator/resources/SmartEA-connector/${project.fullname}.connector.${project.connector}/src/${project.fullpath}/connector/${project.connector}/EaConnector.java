/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package %inject:project.fullname%.connector.%inject:project.connector%;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.emf.cdo.eresource.CDOResource;

import fr.obeo.smartea.core.prism.DocumentRoot;

/**
 * This is the main class for SmartEA connector to ....
 * <p>
 * TODO Detail here
 * </p>
 * 
 * @author %inject:user.name%
 */
public class EaConnector {
    
    private static Properties appProperties;

    /**
    * Gets the application properties.
    *
    * This method returns a Properties object containing the properties needed by the connector.
    * If the properties have not been loaded yet, it will load them from the "application.properties" file.
    *
    * @return The Properties object containing the properties needed by the connector.
    */
    public static Properties getAppProperties() {
        if (appProperties == null) {
            appProperties = loadProperties("application.properties");
        }
        return appProperties;
    }

    public static Properties loadProperties(String fullPath) {
        try {
            Properties result = new Properties();
            File globalPropertiesFile = new File(fullPath);
            try (InputStreamReader fileInputStream = new InputStreamReader(
                    new FileInputStream(globalPropertiesFile),
                    Charset.forName("UTF-8"))) {
                result.load(fileInputStream);
            }
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }
    
    /**
     * Entry point for the SmartEA Connector.
     * 
     * This method establishes the connexion between your connector and the smartEA server.
     *
     * @param args An array of command-line arguments provided to the application.
     *             These arguments may contain configuration information or settings required by the application
     * @throws Exception If any error occurs during the execution of the SmartEA Connector application.
     */
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		try (SmartEAConnectorClient client = new SmartEAConnectorClient(args)) {
			CDOResource semanticResource = (CDOResource) client.getSemanticResource();

			DocumentRoot prismModel = client.getPrismModel();

			// TODO Add business code here

			client.commit();
		}
		long duration = System.currentTimeMillis() - startTime;
		Logger.getAnonymousLogger().info(String.format("Elapsed time: %.2f seconds.", duration / 1_000.));
	}

}
