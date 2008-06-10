/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.page;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.workflow.flow.PeerToPeerWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsPeerReviewWorkPageDefinition extends WorkPageDefinition {

   public static String ID = PeerToPeerWorkflowDefinition.ID + "." + PeerToPeerReviewArtifact.State.Review.name();

   public AtsPeerReviewWorkPageDefinition() {
      this(PeerToPeerReviewArtifact.State.Review.name(), ID, null);
   }

   public AtsPeerReviewWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(ATSAttributes.ROLE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.REVIEW_DEFECT_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
      addWorkItem(AtsWorkDefinitions.ATS_DESCRIPTION_NOT_REQUIRED_ID);
   }

}
