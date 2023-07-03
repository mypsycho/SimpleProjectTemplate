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
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.cs.Part;

import com.siemens.eda.vseanalysis.VseAnalysis.VseElement;

/**
 * Services to queries through %inject:project.name% model.
 * 
 * @author %inject:user.name%
 *
 */
public class ${project.prefix}ModelQueries {

    /**
     * Returns the ancestor matching providing type.
     * <p>
     * Result can it-self. Returns null if no ancestor matchs.
     * </p>
     * @param <T> Type of ancestor
     * @param it element to get the container from
     * @param expectedType ancestor type
     * @return ancestor or null
     */
    public <T extends EObject> static T eContainer(EObject it, Class<T> expectedType) {
        // Not provided by EcoreUtil ...
        if (it == null || expectedType.isInstance(it)) {
            return it;
        }
        return eContainer(it.eContainer(), expectedType);
    }


    /**
     * Returns associated a %inject:project.name% extension.
     * <p>
     * Null if it is null or not defined. For Part, retrieves extension of type.
     * </p>
     *
     * @param it
     *     to evaluate
     * @return true if extended
     */
    public static <T extends %inject:project.prefix%Element> T get%inject:project.prefix%Extension(Class<T> extType, CapellaElement it) {
        if (it == null) {
            return null;
        }
        if (it instanceof Part) {
            CapellaElement type = ((Part) it).getType();
            return type != null
                ? get%inject:project.prefix%Extension(extType, type)
                : null;
        }

        return it.getOwnedExtensions().stream()
            .filter(extType::isInstance)
            .map(extType::cast)
            .findFirst()
            .orElse(null);
    }
    
}
