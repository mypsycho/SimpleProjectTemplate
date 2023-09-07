package %inject:project.fullname%.design.description

import org.eclipse.sirius.diagram.EdgeArrows
import org.eclipse.sirius.diagram.LineStyle
import org.eclipse.sirius.diagram.description.DiagramExtensionDescription
import org.eclipse.sirius.diagram.description.EdgeMapping
import org.eclipse.sirius.diagram.description.NodeMapping
import org.eclipse.sirius.diagram.description.style.EdgeStyleDescription
import org.eclipse.sirius.diagram.description.style.WorkspaceImageDescription
import org.eclipse.sirius.diagram.description.tool.DeleteElementDescription
import org.eclipse.sirius.diagram.description.tool.EdgeCreationDescription
import org.eclipse.sirius.viewpoint.FontFormat
import org.mypsycho.modit.emf.sirius.api.AbstractDiagramExtension
import org.mypsycho.modit.emf.sirius.api.AbstractGroup

import static extension org.mypsycho.modit.emf.sirius.api.SiriusDesigns.*

/**
 * Sirius Elements shared by diagrams.
 */
class SharedDiagramPart extends AbstractDiagramExtension {
	
	/** Tool id for name editing. */
	public static val SIMPLE_EDIT = "simpleEditName"
	
	new(AbstractGroup parent, String alias) {
		super(parent)
		contentAlias = alias
	}

	override initContent(DiagramExtensionDescription it) {
		throw new UnsupportedOperationException("Utility method: do not instantiate")
	}
	
}