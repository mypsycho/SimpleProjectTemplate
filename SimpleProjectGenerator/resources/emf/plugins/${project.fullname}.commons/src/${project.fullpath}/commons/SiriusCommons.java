/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package %inject:project.fullname%.commons;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Services for commons services for Sirius edition.
 *
 * @author %inject:user.name%
 */
public class SiriusCommons {

    /**
     * Returns whether the second object is the same as the first object, or is
     * directly or indirectly contained by the first object.
     * <p>
     * I.e., whether the second object is in the {@link EObject#eContents content tree}
     * of the first. Container proxies are not resolved.
     * </p>
     * <p>
     * This method uses {@link EcoreUtil.isEAncestor } in accessible way for Sirius.
     * </p>
     *
     * @param ancestorEObject
     *     the ancestor object in question.
     * @param eObject
     *     the object to test.
     * @return whether the first object is an ancestor of the second object.
     * @see EObject#eContainer
     */
    public boolean isEAncestor(EObject parent, EObject child) {
        return EcoreUtil.isAncestor(parent, child);
    }

    /**
     * Adds an element to a reference.
     *
     * @param it
     *     containing feature
     * @param feature
     *     to add in
     * @param value
     *     to add
     * @return it
     */
    public EObject eAdd(EObject it, String feature, EObject value) {
        return eAdd(it, (EReference) it.eClass().getEStructuralFeature(feature), value);
    }

    /**
     * Adds an element to a reference.
     *
     * @param it
     *     containing feature
     * @param feature
     *     to add in
     * @param value
     *     to add
     * @return it
     */
    public EObject eAdd(EObject it, EReference feature, EObject value) {
        @SuppressWarnings("unchecked")
        List<EObject> values = (List<EObject>) it.eGet(feature);
        values.add(value);
        return it;
    }

    /**
     * Removes an element from a reference.
     *
     * @param it
     *     containing feature
     * @param feature
     *     to remove from
     * @param value
     *     to remove from
     * @return it
     */
    public EObject eRemove(EObject it, String feature, EObject value) {
        return eRemove(it, (EReference) it.eClass().getEStructuralFeature(feature), value);
    }

    /**
     * Removes an element from a reference.
     *
     * @param it
     *     containing feature
     * @param feature
     *     to remove from
     * @param value
     *     to remove from
     * @return it
     */
    public EObject eRemove(EObject it, EReference feature, EObject value) {
        @SuppressWarnings("unchecked")
        List<EObject> values = (List<EObject>) it.eGet(feature);
        values.remove(value);
        return it;
    }

}
