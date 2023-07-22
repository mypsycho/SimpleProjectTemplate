/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package %inject:project.fullname%.connector.%inject:project.connector%;

import java.util.Optional;

import fr.obeo.smartea.archimate.ArchimateElement;
import fr.obeo.smartea.core.basemm.BaseFactory;
import fr.obeo.smartea.core.basemm.SStereotype;
import fr.obeo.smartea.core.common.util.stereotypes.StereotypeServices;
import fr.obeo.smartea.core.refmodel.Stereotype;
import fr.obeo.smartea.core.refmodel.StereotypeLibrary;


/**
 * Class providing utility methods for handling ArchimateElements and Stereotypes.
 * 
 * @author %inject:user.name%
 */
public class ArchimateElements {

	private static final BaseFactory BASE_FACTORY = BaseFactory.eINSTANCE;

	 /**
     * Creates a SStereotype instance from the given Stereotype.
     *
     * @param stereotype The Stereotype object to create the SStereotype from.
     * 
     * @return The created SStereotype instance with label and ID set based on the given Stereotype.
     */
	public static SStereotype createSStereotype(Stereotype stereotype) {
		SStereotype result = BASE_FACTORY.createSStereotype();
		String stereotypeName = StereotypeServices.getStereotypeName(stereotype);
		result.setLabel(stereotypeName);
		result.setStereotypeId(stereotype.getId());
		return result;
	}

	/**
	 * Sets the value of a property for the given element based on the specified key.
	 *
	 * @param element The ArchimateElement for which the property value needs to be set.
	 * @param key     The key or name of the property whose value is to be set.
	 * @param value   The new value to set for the property.
	 */
	public static void setPropertyValue(ArchimateElement element, String key, String value) {
		element.getProperties().stream()
		    .filter(p -> p.getName().equals(key))
		    .forEach(p -> p.setValue(value));
	}

}
