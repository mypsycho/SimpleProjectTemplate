package %inject:project.fullname%.design.tool

import %inject:project.fullname%.%inject:project.name%.%inject:project.name%Package
import %inject:project.fullname%.design.Activator
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Map
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.sirius.diagram.description.DiagramDescription
import org.eclipse.sirius.viewpoint.description.Group
import org.junit.Test
import org.mypsycho.modit.emf.sirius.api.SiriusDependencies
import org.mypsycho.modit.emf.sirius.tool.SiriusReverseIt
import org.polarsys.capella.core.data.capellacore.CapellacorePackage
import org.polarsys.capella.core.data.cs.CsPackage
import org.polarsys.capella.core.data.fa.FaPackage
import org.polarsys.capella.core.data.information.InformationPackage
import org.polarsys.capella.core.data.pa.PaPackage
import org.polarsys.kitalpha.emde.model.EmdePackage

/**
 * Tool to reverse Sirius design model from '*.design' plugin.
 * <p>
 * Reverse is required when model is modified directly to compare difference with
 * generated model.
 * </p>
 *
 * @author nperansin
 *
 */
class DesignReverse extends DesignToolBase {

	static val EDITED_PKGS = #[
		%inject:project.name%Package.eINSTANCE,
		PaPackage.eINSTANCE,
		CapellacorePackage.eINSTANCE,
		CsPackage.eINSTANCE,
		InformationPackage.eINSTANCE,
		FaPackage.eINSTANCE,
		EmdePackage.eINSTANCE
	]


	@Test
	def void reverseModel() {
		SiriusReverseIt.loadSiriusGroup(Activator.DESIGN_PATH)
			.createSiriusReverseIt(
				Paths.get(REVERSE_PATH), 
				"%inject:project.fullname%.design.description.%inject:project.prefix%Design"
			).perform
	}
	

	static def createSiriusReverseIt(Group content, Path dir, String classname) {
		new SiriusReverseIt(
			content, 
			dir, 
			classname,
			EDITED_PKGS
		) {
			
			override addExplicitExtras(ResourceSet rs, Map<EObject, String> extras) {
				CAPELLA_DESIGNS.forEach[
					extras += SiriusDependencies.getDependencyExtras(it, rs, 
						DesignToolBase.getCapellaDesign(it)
					)
				]
			}
			
			override toClassname(EObject it) {
				if (it instanceof DiagramDescription) {
					// For cloned diagram,
					// avoid Capella original name.
					
				} 
				super.toClassname(it)
			}
			
		}
	}
	
	
}
