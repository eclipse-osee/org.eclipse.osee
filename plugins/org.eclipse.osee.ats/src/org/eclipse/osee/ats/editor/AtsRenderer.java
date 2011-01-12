/*
 * Created on Jan 12, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor;

import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;

public abstract class AtsRenderer extends DefaultArtifactRenderer {

   protected AtsRenderer() {
      super();
   }

   @Override
   public int minimumRanking() throws OseeCoreException {
      if (AccessControlManager.isOseeAdmin()) {
         return NO_MATCH;
      } else {
         return ARTIFACT_TYPE_MATCH;
      }
   }
}
