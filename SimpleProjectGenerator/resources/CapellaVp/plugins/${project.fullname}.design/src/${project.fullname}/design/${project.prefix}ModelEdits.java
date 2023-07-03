/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package %inject:project.fullname%.design;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.sirius.business.api.componentization.ViewpointRegistry;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Services to edit through Sirius VSM.
 * 
 * @author %inject:user.name%
 *
 */
public class ${project.prefix}ModelEdits {

    /**
     * Logs a AQL call in Standard IO.
     * 
     * @param it debugged element
     * @param message to print
     * @return element
     */
    public static final EObject ${project.prefix}Debug(EObject it, String message) {
        System.out.println("VSM-Call " + );
        return it;
    }

    // Public method can access by VSM.
    
}
