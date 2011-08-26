/*
 * Created on Aug 26, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public interface AttributeModifier {

   String modifyForSave(Artifact owner, String value) throws OseeCoreException;
}
