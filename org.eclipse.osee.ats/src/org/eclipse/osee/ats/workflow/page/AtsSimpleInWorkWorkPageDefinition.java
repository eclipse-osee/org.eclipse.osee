/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.page;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.workflow.flow.SimpleWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.SimpleWorkflowDefinition.SimpleState;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsSimpleInWorkWorkPageDefinition extends WorkPageDefinition {

   public static String ID = SimpleWorkflowDefinition.ID + "." + SimpleState.InWork.name();

   public AtsSimpleInWorkWorkPageDefinition() {
      this(SimpleState.InWork.name(), ID, null);
   }

   public AtsSimpleInWorkWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(RuleWorkItemId.atsRequireStateHourSpentPrompt.name());
      addWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
   }
}
