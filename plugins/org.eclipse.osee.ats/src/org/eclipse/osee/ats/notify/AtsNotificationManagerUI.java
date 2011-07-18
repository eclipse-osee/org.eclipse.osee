/*
 * Created on Jul 18, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.notify;

import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.util.Overview.PreviewStyle;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationManagerUI {

   public static String getPreviewHtml(AbstractWorkflowArtifact workflow, PreviewStyle... styles) throws OseeCoreException {
      Overview o = new Overview();
      o.addHeader(workflow, styles);
      o.addFooter(workflow, styles);
      return o.getPage();
   }

}
