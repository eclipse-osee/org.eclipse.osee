/*
 * Created on Jan 12, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor;

import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;

public abstract class AtsRenderer extends DefaultArtifactRenderer {

   protected AtsRenderer() {
      super();
   }

   @Override
   public int minimumRanking() {
      return IRenderer.GENERAL_MATCH;
   }
}
