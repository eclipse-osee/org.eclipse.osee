/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.workflow.page.AtsAnalyzeWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsAuthorizeWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsEndorseWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsImplementWorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkDefinitionProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemBooleanDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData.Fill;

/**
 * Create all the default ATS work items. This keeps from having to create a class for each of these. Also implement
 * WorkDefinitionProvider which registers all definitions with the definitions factory
 * 
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitions implements IWorkDefinitionProvider {

   public static List<WorkItemDefinition> workItems;
   public static Map<String, WorkItemDefinition> idToWorkItem;

   public static enum BooleanWorkItemId {
      atsRequireStateHourSpentPrompt, atsAddDecisionValidateBlockingReview, atsAddDecisionValidateNonBlockingReview
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      if (workItems == null) {
         workItems = new ArrayList<WorkItemDefinition>();

         // Create boolean work items
         workItems.add(new WorkItemBooleanDefinition(BooleanWorkItemId.atsRequireStateHourSpentPrompt.name(), true));
         workItems.add(new WorkItemBooleanDefinition(BooleanWorkItemId.atsAddDecisionValidateBlockingReview.name(),
               true));
         workItems.add(new WorkItemBooleanDefinition(BooleanWorkItemId.atsAddDecisionValidateNonBlockingReview.name(),
               false));

         // Create AtsAttribute work items
         workItems.add(new AtsAttributeSoleStringXWidgetWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE, Fill.Vertically));
         workItems.add(new AtsAttributeSoleStringXWidgetWorkItem(ATSAttributes.PROBLEM_ATTRIBUTE, Fill.Vertically));
         workItems.add(new AtsAttributeSoleStringXWidgetWorkItem(ATSAttributes.PROPOSED_RESOLUTION_ATTRIBUTE,
               Fill.Vertically));
         workItems.add(new AtsAttributeSoleStringXWidgetWorkItem(ATSAttributes.DESCRIPTION_ATTRIBUTE, Fill.Vertically));
         workItems.add(new AtsAttributeSoleFloatXWidgetWorkItem(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE));
         workItems.add(new AtsAttributeSoleComboXWidgetWorkItem(ATSAttributes.CHANGE_TYPE_ATTRIBUTE,
               "OPTIONS_FROM_ATTRIBUTE_VALIDITY"));
         workItems.add(new AtsAttributeSoleComboXWidgetWorkItem(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE,
               "OPTIONS_FROM_ATTRIBUTE_VALIDITY"));
         workItems.add(new AtsAttributeSoleDateXWidgetWorkItem(ATSAttributes.DEADLINE_ATTRIBUTE));

         // Add Page Definitions
         workItems.add(new AtsEndorseWorkPageDefinition());
         workItems.add(new AtsAnalyzeWorkPageDefinition());
         workItems.add(new AtsAuthorizeWorkPageDefinition());
         workItems.add(new AtsImplementWorkPageDefinition());
         workItems.add(new AtsCompletedWorkPageDefinition());
         workItems.add(new AtsCancelledWorkPageDefinition());
      }
      return workItems;
   }

   public static WorkItemDefinition getWorkItemDefinition(ATSAttributes atsAttribute) {
      return getWorkItemDefinition(atsAttribute.getStoreName());
   }

   public static WorkItemDefinition getWorkItemDefinition(String id) {
      if (idToWorkItem == null) {
         idToWorkItem = new HashMap<String, WorkItemDefinition>();
         for (WorkItemDefinition def : getAtsWorkDefinitions()) {
            idToWorkItem.put(def.getId(), def);
         }
      }
      return idToWorkItem.get(id);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkDefinitionProvider#getWorkItemDefinitions()
    */
   @Override
   public Collection<WorkItemDefinition> getWorkItemDefinitions() {
      return AtsWorkDefinitions.getAtsWorkDefinitions();
   }
}
