/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package %inject:project.fullname%.%inject:param.name%;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.sirius.business.api.componentization.ViewpointRegistry;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.eclipse.core.runtime;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author %inject:user.name%
 *
 */
public class Activator extends Plugin {

    /** The plug-in ID. */
    public static final String PLUGIN_ID = "%inject:project.fullname%.%inject:param.name%";

    // The shared instance
    private static Activator plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;

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
