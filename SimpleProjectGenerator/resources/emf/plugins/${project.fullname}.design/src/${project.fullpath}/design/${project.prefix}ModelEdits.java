/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package %inject:project.fullname%.design;

import org.eclipse.emf.ecore.EObject;

/**
 * Services to edit models through Sirius VSM.
 * 
 * @author %inject:user.name%
 */
public class %inject:project.prefix%ModelEdits {

    /**
     * Logs a AQL call in IO/Standard stream.
     * <p>
     * Should only be used during development time.
     * </p>
     * 
     * @param it debugged element
     * @param message to print
     * @return provided element
     */
    @Deprecated
    public static final EObject to%inject:project.prefix%Debug(EObject it, String message) {
        System.out.println("VSM-Call " + message + ": " + it);  //$NON-NLS-1$ //$NON-NLS-2$
        return it;
    }

    // Public static method accessible by VSM.
    // This class contains modifying methods.
    
}
