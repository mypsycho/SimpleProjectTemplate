/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package %inject:project.fullname%.commons;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Simple methods for EMF-based plugin.
 * 
 * @author %inject:user.name%
 */
public class Emfs {

    private final static String MEDIT_PREFIX = "_UI_"; //$NON-NLS-1$

    /**
     * Returns the ancestor matching providing type.
     * <p>
     * Result can it-self. Returns null if no ancestor matchs.
     * </p>
     *
     * @param <T>
     *     Type of ancestor
     * @param it
     *     element to get the container from
     * @param expectedType
     *     ancestor type
     * @return ancestor or null
     */
    @SuppressWarnings("unchecked")
    public static <T extends EObject> T eContainer(EObject it, Class<T> expectedType) {
        // Not provided by EcoreUtil ...
        if (it == null || expectedType.isInstance(it)) {
            return (T) it;
        }
        return eContainer(it.eContainer(), expectedType);
    }

    /**
     * Provides a label using element type.
     *
     * @param rl
     *     resource locator
     * @param object
     *     to get the label from.
     * @return a label
     */
    public static String getTypeBasedLabel(ResourceLocator rl, Object object) {
        String label;
        if (object instanceof EObject) {
            EObject element = (EObject) object;
            label = rl.getString(getEClassKey(element.eClass()));
        } else if (object != null) {
            label = object.getClass().getSimpleName();
        } else {
            label = "<void>"; //$NON-NLS-1$
        }
        return '[' + label + ']';
    }

    /**
     * Returns the key to locate the label of an class.
     *
     * @param type
     *     to display
     * @return EMF edit key
     */
    public static final String getEClassKey(EClass type) {
        return MEDIT_PREFIX + type.getName()
            + "_type"; //$NON-NLS-1$
    }

    /**
     * Returns the key to locate the label of a feature.
     *
     * @param feature
     *     to display
     * @return EMF edit key
     */
    public static final String getEFeatureKey(EStructuralFeature feature) {
        return MEDIT_PREFIX + feature.getEContainingClass().getName()
            + "_" + feature.getName() //$NON-NLS-1$
            + "_feature"; //$NON-NLS-1$
    }

    /**
     * Returns the key to locate the label of a enum literal.
     *
     * @param literal
     *     to display
     * @return EMF edit key
     */
    public static final String getEEnumLiteralKey(Enumerator literal) {
        // Only works if genmodel does not temper the name
        return MEDIT_PREFIX + literal.getClass().getSimpleName()
            + "_" + literal.getLiteral() //$NON-NLS-1$
            + "_literal"; //$NON-NLS-1$
    }

    /**
     * Label provider for EMF structural elements.
     */
    public interface LabelProvider {

        /**
         * Returns the label of a EClass.
         *
         * @param it
         *     EClass
         */
        String getLabel(EClass it);

        /**
         * Returns the label of a feature.
         *
         * @param it
         *     reference
         */
        String getLabel(EStructuralFeature it);

        /**
         * Returns the label of a enum litteral.
         *
         * @param it
         *     enum literal
         */
        String getLabel(Enumerator it);

    }


    /**
     * Label provider for EMF structural elements based on
     */
    public static class SimpleLabelProvider implements LabelProvider {

        private final ResourceLocator locator;

        public SimpleLabelProvider(ResourceLocator rl) {
            locator = rl;
        }

        @Override
        public String getLabel(EClass it) {
            return locator.getString(getEClassKey(it));
        }

        @Override
        public String getLabel(EStructuralFeature it) {
            return locator.getString(getEFeatureKey(it));
        }

        @Override
        public String getLabel(Enumerator it) {
            return locator.getString(getEEnumLiteralKey(it));
        }

    }

}

