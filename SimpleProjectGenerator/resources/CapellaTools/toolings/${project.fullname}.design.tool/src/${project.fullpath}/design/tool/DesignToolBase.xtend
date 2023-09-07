package %inject:project.fullname%.design.tool

import %inject:project.fullname%.design.Activator
import java.nio.file.Paths

/**
 * Common elements for tool of dynamic Design model of '*.sirius' plugin.
 * 
 * @author Obeo
 */
class DesignToolBase {

	// Specific
	protected static val PLUGINS_PATH = "../../plugins"
	
	public static val CAPELLA_DESIGNS = "common,context,EPBS,logical,oa,physical".split(",")

	public static val CAPELLA_DESIGN_PLUGIN = "org.polarsys.capella.core.sirius.analysis"
		
	protected static val REVERSE_PATH = "target/rvs"
	
	// Derived
	protected static val ODESIGN_FILE = Paths.get(PLUGINS_PATH)
		// /!\ File path matches java bundle path.
		.resolve(Activator.DESIGN_PATH)
		.toAbsolutePath
    
	
	static def String getCapellaDesign(String part) {
		'''«CAPELLA_DESIGN_PLUGIN»/description/«part».odesign'''
	}

}
