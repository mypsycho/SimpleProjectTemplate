/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/

 package %inject:project.fullname%.connector.%inject:project.connector%;

import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.ecore.resource.Resource;

import fr.obeo.smartea.core.common.CDOProtocol;
import fr.obeo.smartea.core.common.CommonConstants;
import fr.obeo.smartea.core.common.api.client.SmartEAProtocol;
import fr.obeo.smartea.core.prism.DocumentRoot;
import fr.obeo.smartea.core.server.api.db.DbProjectContentCtx;
import fr.obeo.smartea.core.server.api.db.DbProjectContentTx;
import fr.obeo.smartea.core.server.api.db.DbProjectInternalTx;
import fr.obeo.smartea.core.server.client.SmartEAClient;
import fr.obeo.smartea.core.server.client.SmartEAProjectClient;

/**
 * Simple class to connect to the smartEA server.
 * 
 * @author %inject:user.name%
 */
public class SmartEAConnectorClient implements AutoCloseable {

	private int nbElement = 0;
	private static final Logger logger = Logger.getAnonymousLogger();

	private SmartEAClient client;
	private SmartEAProjectClient projectClient;

	
	/**
	 * Constructs a new SmartEAConnectorClient with the provided command-line arguments.
	 * <p>
	 * The array should contain the following elements:
	 * <ol start="0">
     * <li>HTTP host (e.g., hostname or IP address)</li>
     * <li>The HTTP port the smartEA server uses (e.g., integer value)</li>
     * <li>The ID to connect to the smartEA server (e.g., String)</li>
     * <li>The password to connect to the smartEA server (e.g., String)</li>
     * <li>The Id of the project we want to access (e.g., String)</li>
	 * </ol>
	 * </p>
	 *
	 * @param args An array of command-line arguments required to initialize the SmartEA client and project.
	 * @throws IOException If an I/O error occurs while initializing the SmartEA client.
	 */
	public SmartEAConnectorClient(String[] args) throws IOException {
		// Initialize SmartEA Client
		client = new SmartEAClient(SmartEAProtocol.HTTP, // Protocol
				args[0], // HTTP host
				Integer.parseInt(args[1]), // HTTP port
				args[2], // User ID
				args[3], // Password
				CDOProtocol.WS, 7200, 3600, true // Read only mode for internal db context
		);
		projectClient = client.newProjectClient(args[4], // Project ID
				false // Write mode
		);
	}

	public Resource getSemanticResource() {
		// Retrieve the semantic resource
		DbProjectContentCtx ctx = projectClient.getProjectContentCtx();
		DbProjectContentTx tx = ctx.getTx(ctx.getMasterBranch());

		return tx.getSemanticResource();
	}

	public DocumentRoot getPrismModel() {
		// Retrieve the semantic resource
		DbProjectInternalTx mainTx = projectClient.getProjectInternalCtx().getTx();
		CDOResource mainResource = mainTx.getResource(CommonConstants.PRISM_SEMANTIC_URI);
		return (DocumentRoot) mainResource.getContents().get(0);
	}

	/**
	 * Commits the changes made on the server.
	 * 
	 * @throws IllegalStateException
	 *     Thrown if there is a commit exception.
	 */
	public void commit()  {
        try {
            projectClient.commit();
        } catch (CommitException e) {
            throw new IllegalStateException(e);
        }
	}

	@Override
	public void close() throws Exception {
		// Release context
		projectClient.release();
		client.release();
	}


}
