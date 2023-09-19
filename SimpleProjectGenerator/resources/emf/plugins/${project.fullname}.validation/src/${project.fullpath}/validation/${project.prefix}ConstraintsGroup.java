/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package %inject:project.fullname%.rules.validation;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.Category;
import org.eclipse.emf.validation.model.CategoryManager;
import org.eclipse.emf.validation.model.ConstraintSeverity;
import org.eclipse.emf.validation.model.EvaluationMode;
import org.eclipse.emf.validation.model.IModelConstraint;
import org.eclipse.emf.validation.service.AbstractConstraintDescriptor;
import org.eclipse.emf.validation.service.ConstraintExistsException;
import org.eclipse.emf.validation.service.ConstraintRegistry;
import org.eclipse.emf.validation.service.IModelConstraintProvider;
import org.eclipse.osgi.util.NLS;

import %inject:project.fullname%.%inject:project.prefix%Element;

/**
 * Group of EMF constraints to evaluate during validation.
 * 
 * @author %inject:user.name%
 */
public class %inject:project.prefix%ConstraintsGroup implements IModelConstraintProvider {

    private static final ILog LOGGER = Platform.getLog(AbstractModelConstraintProvider.class);
    
    @Override
    public Collection<IModelConstraint> getLiveConstraints(
            Notification notification, Collection<IModelConstraint> constraints) {
        // Live constraint blocks transaction.
        return Collections.emptyList();
    }

    @Override
    public Collection<IModelConstraint> getBatchConstraints(
            EObject eObject, Collection<IModelConstraint> constraints) {
        Collection<IModelConstraint> result = constraints != null
            ? constraints
            : new ArrayList<>();
            
        // TODO add constraints to result
        return result;
    }

}