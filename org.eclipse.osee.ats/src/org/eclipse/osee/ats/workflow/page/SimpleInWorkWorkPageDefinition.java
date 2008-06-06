/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.page;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.SimpleWorkflowDefinition.SimpleState;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class SimpleInWorkWorkPageDefinition extends WorkPageDefinition {

   public static String ID = TeamWorkflowDefinition.ID + "." + SimpleState.InWork.name();

   public SimpleInWorkWorkPageDefinition() {
      this(SimpleState.InWork.name(), ID, null);
   }

   public SimpleInWorkWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(RuleWorkItemId.atsRequireStateHourSpentPrompt.name());
      addWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
   }
}
