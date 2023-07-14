/*******************************************************************************
 * %inject:project.copyright%
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package %inject:project.fullname%.%inject:project.name%.provider.util;

import %inject:project.fullname%.%inject:project.name%.%inject:project.name%EditPlugin;
import com.siemens.eda.vseanalysis.commons.Emfs;

/**
 * Constants and util methods for labels of Meta-model.
 *
 * @author %inject:user.name%
 */
public class %inject:project.prefix%Labels {

    /** Label provider for %inject:project.name% meta-model. */
    public static Emfs.LabelProvider INSTANCE = new Emfs.SimpleLabelProvider(%inject:project.name%EditPlugin.INSTANCE);

}

