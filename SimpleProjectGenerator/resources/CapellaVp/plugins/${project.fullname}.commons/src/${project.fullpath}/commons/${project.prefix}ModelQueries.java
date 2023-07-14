/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package %inject:project.fullname%.commons;

import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.cs.Part;

import %inject:project.fullname%.%inject:project.prefix%Element;

/**
 * Services to queries through %inject:project.name% model.
 * 
 * @author %inject:user.name%
 */
public class %inject:project.prefix%ModelQueries {
    
    /**
     * Returns associated a %inject:project.name% extension.
     * <p>
     * Null if it is null or not defined. For Part, retrieves extension of type.
     * </p>
     *
     * @param extType expected extension type
     * @param it
     *     to evaluate
     * @return extension or null
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
    

    /**
     * Returns associated a %inject:project.name% extension.
     * <p>
     * Null if it is null or not defined. For Part, retrieves extension of type.
     * </p>
     *
     * @param it
     *     to evaluate
     * @return extension or null
     */
    public static %inject:project.prefix%Element get%inject:project.prefix%Extension(CapellaElement it) {
        return get%inject:project.prefix%Extension(%inject:project.prefix%Element.class, it);
    }
    
    /**
     * Indicates if a Capella element is extended.
     *
     * @param it
     *     to evaluate
     * @return extension or null
     */
    public static <T extends %inject:project.prefix%Element> T is%inject:project.prefix%Extented(CapellaElement it) {
        return get%inject:project.prefix%Extension(it) != null;
    }
}
