package %inject:project.fullname%.design.description

import %inject:project.fullname%.%inject:project.name%.%inject:project.name%Package
import %inject:project.fullname%.design.%inject:project.prefix%ModelEdits
import %inject:project.fullname%.commons.%inject:project.prefix%ModelQueries
import org.eclipse.sirius.viewpoint.description.Group
import org.eclipse.sirius.viewpoint.description.Viewpoint
import org.eclipse.sirius.viewpoint.description.tool.ToolEntry
import org.mypsycho.modit.emf.sirius.api.AbstractGroup
import org.mypsycho.modit.emf.sirius.api.SiriusDependencies
import org.polarsys.capella.common.data.activity.ActivityPackage
import org.polarsys.capella.core.data.capellacore.CapellacorePackage
import org.polarsys.capella.core.data.cs.CsPackage
import org.polarsys.capella.core.data.fa.FaPackage
import org.polarsys.capella.core.data.information.InformationPackage
import org.polarsys.capella.core.data.information.communication.CommunicationPackage
import org.polarsys.capella.core.data.information.datatype.DatatypePackage
import org.polarsys.capella.core.data.information.datavalue.DatavaluePackage
import org.polarsys.capella.core.data.oa.OaPackage
import org.polarsys.capella.core.sirius.analysis.ABServices
import org.polarsys.capella.core.sirius.analysis.CapellaServices
import org.polarsys.capella.core.sirius.analysis.CsServices
import org.polarsys.capella.core.sirius.analysis.DFServices
import org.polarsys.capella.core.sirius.analysis.FaServices
import org.polarsys.capella.core.sirius.analysis.FunctionalChainServices
import org.polarsys.capella.core.sirius.analysis.OAServices
import org.polarsys.kitalpha.emde.model.EmdePackage

import static %inject:project.fullname%.design.tool.DesignToolBase.*

/**
 * Main class for ODesign model.
 */
class %inject:project.prefix%Design extends AbstractGroup {

	// Capella icons contains gifs.
	static public val CAPELLA_EDIT_ICON_PATH = "/org.polarsys.capella.core.data.res.edit/icons/full/obj16/"
	static public val CAPELLA_ICON_PATH = "/org.polarsys.capella.core.sirius.analysis/icons/full/obj16/"
	static public val CAPELLA_SVG_PATH = "/org.polarsys.capella.core.sirius.analysis/description/images/"
	
	
	static public val VPDSL_ICON_PATH = "/%inject:project.fullname%.vpdsl/icons/";
	static public val EDIT_ICON_PATH = "/%inject:project.fullname%.model.edit/icons/full/obj16/";

	static public val LOCAL_ICON_PATH = "/%inject:project.fullname%.design/icons/";
	
	
	new () {
        businessPackages += #[
			%inject:project.name%Package,
			CapellacorePackage.eINSTANCE,
			CsPackage.eINSTANCE,
			FaPackage.eINSTANCE,
			InformationPackage.eINSTANCE,
			CommunicationPackage.eINSTANCE,
			DatatypePackage.eINSTANCE,
			DatavaluePackage.eINSTANCE,
			OaPackage.eINSTANCE,
			// /*pa.*/DeploymentPackage.eINSTANCE,
			EmdePackage.eINSTANCE,
			ActivityPackage.eINSTANCE
        ]
	}

	override initContent(Group it) {
		name = "%inject:project.name%"
		version = "12.0.0.2017041100"
		ownedViewpoints += Viewpoint.create [
			name = "%inject:project.name%_ID"
			label = "%%inject:project.prefix%.vp.label"
			modelFileExtension = "capella"
			
			// ownedRepresentations += new %inject:project.prefix%??.Diagram(this).createContent
			// ownedRepresentationExtensions += new %inject:project.prefix%??DiagramExtension(this).createContent
			
			use(DFServices)
			use(CsServices)
			use(CapellaServices)
			use(FaServices)
			use(ABServices)
			use(OAServices)
			use(FunctionalChainServices)
			use(%inject:project.prefix%ModelEdits)
			use(%inject:project.prefix%ModelQueries)
		]
		
	}

	override initExtras() {
		super.initExtras
				
		CAPELLA_DESIGNS.forEach[
			extras += SiriusDependencies.getDependencyExtras(it, 
				resource.resourceSet, 
				getCapellaDesign(it)
			).entrySet.toMap([ value ])[ key ]
		]
		
	}

	def context() { this }
	
	static def <T extends ToolEntry> tooltip(T it) {
		documentation = label + ".ttips"
		it
	}

}
