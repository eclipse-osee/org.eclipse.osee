/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.flow;

import org.eclipse.osee.ats.workflow.page.AtsAnalyzeWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsAuthorizeWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsEndorseWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsImplementWorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class DefaultTeamWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.defaultTeam";

   public DefaultTeamWorkflowDefinition() {
      this("Default Team Workflow Definition", ID);
   }

   /**
    * @param name
    * @param id
    * @param parentId
    */
   public DefaultTeamWorkflowDefinition(String name, String id) {
      super(name, id, null);
      addPageTransition(AtsEndorseWorkPageDefinition.ID, TransitionType.DefaultToPage, AtsAnalyzeWorkPageDefinition.ID);
      addPageTransition(AtsAnalyzeWorkPageDefinition.ID, TransitionType.DefaultToPage,
            AtsAuthorizeWorkPageDefinition.ID);
      addPageTransition(AtsAuthorizeWorkPageDefinition.ID, TransitionType.DefaultToPage,
            AtsImplementWorkPageDefinition.ID);
      addPageTransition(AtsImplementWorkPageDefinition.ID, TransitionType.DefaultToPage,
            AtsCompletedWorkPageDefinition.ID);

      // Add return transitions
      addPageTransition(AtsAuthorizeWorkPageDefinition.ID, TransitionType.ReturnPage, AtsAnalyzeWorkPageDefinition.ID);
      addPageTransition(AtsImplementWorkPageDefinition.ID, TransitionType.ReturnPage, AtsAnalyzeWorkPageDefinition.ID);
      addPageTransition(AtsImplementWorkPageDefinition.ID, TransitionType.ReturnPage, AtsAuthorizeWorkPageDefinition.ID);

      // Add cancelled transitions
      addPageTransition(AtsEndorseWorkPageDefinition.ID, TransitionType.ToFromPage, AtsCancelledWorkPageDefinition.ID);
      addPageTransition(AtsAnalyzeWorkPageDefinition.ID, TransitionType.ToFromPage, AtsCancelledWorkPageDefinition.ID);
      addPageTransition(AtsAuthorizeWorkPageDefinition.ID, TransitionType.ToFromPage, AtsCancelledWorkPageDefinition.ID);
      addPageTransition(AtsImplementWorkPageDefinition.ID, TransitionType.ToFromPage, AtsCancelledWorkPageDefinition.ID);
   }

   public WorkPageDefinition getStartWorkPage() {
      return (WorkPageDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(AtsEndorseWorkPageDefinition.ID);

   }

   public String toString() {
      return getPageNames().toString();
   }

}
