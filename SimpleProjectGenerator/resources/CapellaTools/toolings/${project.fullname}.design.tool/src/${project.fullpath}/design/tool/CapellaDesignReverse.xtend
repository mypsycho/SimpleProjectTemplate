package %inject:project.fullname%.design.tool

import java.nio.file.Files
import java.nio.file.Paths
import java.util.Map
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.junit.Test
import org.mypsycho.modit.emf.sirius.api.SiriusDependencies
import org.mypsycho.modit.emf.sirius.tool.SiriusReverseIt

/**
 * Tool to reverse Sirius design model from '*.design' plugin.
 * <p>
 * Reverse is required when model is modified directly to compare difference with
 * generated model.
 * </p>
 *
 * @author %inject:user.name%
 *
 */
class CapellaDesignReverse extends DesignToolBase {
			
	@Test
	def void reverseModel() {
		
		CAPELLA_DESIGNS.forEach[
			val others = CAPELLA_DESIGNS.filter[ other | other != it ].toList
			
			new SiriusReverseIt(
				capellaDesign, 
				Paths.get(REVERSE_PATH), 
				'''«CAPELLA_DESIGN_PLUGIN».«it».«toFirstUpper»Design'''
			) {
				override addExplicitExtras(ResourceSet rs, Map<EObject, String> extras) {
					others.forEach[
						extras += SiriusDependencies.getDependencyExtras(it, rs, capellaDesign)
					]
				}
			}.perform
			
			var uri = URI.createPlatformPluginURI(capellaDesign, true)
			
			val out = Files.newOutputStream(Paths.get('''«REVERSE_PATH»/«it».odesign'''))
			try {
				new ResourceSetImpl().getResource(uri, true).save(out, #{})
			} finally {
				out.close
			}
			
		]
	}


	
}
