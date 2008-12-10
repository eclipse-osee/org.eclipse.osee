/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.page;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsAuthorizeWorkPageDefinition extends WorkPageDefinition {

   public static String ID = TeamWorkflowDefinition.ID + "." + DefaultTeamState.Authorize.name();

   public AtsAuthorizeWorkPageDefinition() {
      this(DefaultTeamState.Authorize.name(), ID, null);
   }

   public AtsAuthorizeWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(ATSAttributes.WORK_PACKAGE_ATTRIBUTE.getStoreName());
   }

}
