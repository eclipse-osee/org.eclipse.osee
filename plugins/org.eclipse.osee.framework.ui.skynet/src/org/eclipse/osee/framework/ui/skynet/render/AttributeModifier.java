/*
 * Created on Aug 26, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public interface AttributeModifier {

   InputStream modifyForSave(Artifact owner, File file) throws OseeCoreException, IOException;
}
