/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package %inject:project.fullname%.design;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.sirius.business.api.componentization.ViewpointRegistry;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author %inject:user.name%
 *
 */
public class Activator extends AbstractUIPlugin {

    /** The plug-in ID. */
    public static final String PLUGIN_ID = "%inject:project.fullname%.design";

    /** The Odesign path. */
    public static final String DESIGN_PATH = PLUGIN_ID + "/description/%inject:project.name%.odesign";

    // The shared instance
    private static Activator plugin;

    private final Set<Viewpoint> viewpoints = new HashSet<>();

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        
        plugin = this;
        viewpoints.addAll(ViewpointRegistry.getInstance().registerFromPlugin(DESIGN_PATH));
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        for (Viewpoint viewpoint : viewpoints) {
            ViewpointRegistry.getInstance().disposeFromPlugin(viewpoint);
        }
        viewpoints.clear();

        super.stop(context);
    }

    /**
     * Returns the shared instance.
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

}
