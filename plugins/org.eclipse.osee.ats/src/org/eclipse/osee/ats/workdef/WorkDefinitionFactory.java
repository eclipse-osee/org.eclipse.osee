/*
 * Created on Dec 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.actions.wizard.IAtsTeamWorkflow;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.workflow.flow.DecisionWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.GoalWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.PeerToPeerWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TaskWorkflowDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

public class WorkDefinitionFactory {

   private static final Map<String, RuleDefinition> idToRule = new HashMap<String, RuleDefinition>();
   private static final Set<WorkDefinition> workDefinitions = new HashSet<WorkDefinition>();

   public static RuleDefinition getRuleById(String id) {
      ensureRulesLoaded();
      return idToRule.get(id);
   }

   private synchronized static void ensureRulesLoaded() {
      if (idToRule.isEmpty()) {
         try {
            for (WorkItemDefinition workItem : WorkItemDefinitionFactory.getWorkItemDefinitions()) {
               if (workItem instanceof WorkRuleDefinition) {
                  WorkRuleDefinition workRule = (WorkRuleDefinition) workItem;
                  RuleDefinition ruleDef = new RuleDefinition(workRule.getId());
                  ruleDef.setDescription(workRule.getDescription());
                  copyKeyValuePair(ruleDef, workRule);
                  idToRule.put(ruleDef.getName(), ruleDef);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   public static WorkDefinition getWorkDefinition(Artifact artifact) throws OseeCoreException {
      WorkDefinition workDef = getWorkDefinitionNew(artifact);
      if (workDef == null) {
         workDef = translateToWorkDefinition(WorkFlowDefinitionFactory.getWorkFlowDefinition(artifact));
      }
      workDefinitions.add(workDef);
      return workDef;
   }

   public static WorkDefinition getWorkDefinition(String id) throws OseeCoreException {
      WorkDefinition workDef = null;
      for (IAtsWorkDefinitionProvider provider : AtsWorkDefinitionProviders.getAtsTeamWorkflowExtensions()) {
         workDef = provider.getWorkFlowDefinition(id);
         if (workDef != null) {
            break;
         }
      }
      if (workDef == null) {
         workDef = translateToWorkDefinition((WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(id));
      }
      workDefinitions.add(workDef);
      return workDef;
   }

   private static WorkDefinition translateToWorkDefinition(WorkFlowDefinition workFlowDef) {
      ensureRulesLoaded();
      try {
         String startWorkPageName = workFlowDef.getStartPageId();
         WorkDefinition workDef = new WorkDefinition(workFlowDef.getId());
         for (WorkPageDefinition workPage : workFlowDef.getPages()) {
            // not using ids anymore for states, widgets or rules
            StateDefinition stateDef = getOrCreateState(workDef, workPage.getPageName());
            workDef.getStates().add(stateDef);
            stateDef.setOrdinal(workPage.getWorkPageOrdinal());
            if (workPage.getPageName().equals(startWorkPageName)) {
               stateDef.setStartState(true);
            }
            copyKeyValuePair(stateDef, workPage);
            stateDef.setWorkDefinition(workDef);
            if (workFlowDef.getStartPage().getId().equals(workPage.getId())) {
               stateDef.setStartState(true);
            }
            stateDef.setWorkPageType(workPage.getWorkPageType());
            for (WorkPageDefinition returnPageDefinition : workFlowDef.getReturnPages(workPage)) {
               StateDefinition returnStateDef = getOrCreateState(workDef, returnPageDefinition.getPageName());
               stateDef.getReturnStates().add(returnStateDef);
            }
            for (WorkPageDefinition toPageDefinition : workFlowDef.getToPages(workPage)) {
               StateDefinition toStateDef = getOrCreateState(workDef, toPageDefinition.getPageName());
               stateDef.getToStates().add(toStateDef);
            }
            WorkPageDefinition defaultToPageDefinition = workFlowDef.getDefaultToPage(workPage);
            if (defaultToPageDefinition != null) {
               StateDefinition defaultToStateDef = getOrCreateState(workDef, defaultToPageDefinition.getPageName());
               stateDef.getDefaultToStates().add(defaultToStateDef);
            }

            CompositeStateItem compStateItem = null;
            for (WorkItemDefinition itemDef : workPage.getWorkItems(true)) {
               if (itemDef instanceof WorkWidgetDefinition) {
                  WorkWidgetDefinition workWidget = (WorkWidgetDefinition) itemDef;
                  DynamicXWidgetLayoutData data = workWidget.get();

                  // Check for being_composites and process as separate widgets
                  int numColumns = data.getBeginComposite();
                  if (numColumns > 0) {
                     compStateItem = new CompositeStateItem(numColumns);
                     stateDef.getStateItems().add(compStateItem);
                  }

                  // Create new Widget Definition for widget
                  WidgetDefinition widgetDef = new WidgetDefinition(workWidget.getName());
                  widgetDef.setDescription(workWidget.getDescription());
                  copyKeyValuePair(widgetDef, workWidget);

                  // Convert layout data to widget definition values
                  for (XOption option : data.getXOptionHandler().getXOptions()) {
                     WidgetOption widgetOpt = null;
                     try {
                        widgetOpt = WidgetOption.valueOf(option.name());
                     } catch (IllegalArgumentException ex) {
                        // do nothing
                     }
                     if (widgetOpt != null) {
                        widgetDef.set(widgetOpt);
                     }
                  }
                  // Old model only had required, new model has two types of required
                  if (data.isRequired()) {
                     widgetDef.set(WidgetOption.REQUIRED_FOR_TRANSITION);
                  }
                  if (Strings.isValid(data.getName()) && !widgetDef.getName().equals(data.getName())) {
                     widgetDef.setName(data.getName());
                  }
                  widgetDef.setStoreName(data.getStoreName());
                  widgetDef.setDefaultValue(data.getDefaultValue());
                  widgetDef.setHeight(data.getHeight());
                  widgetDef.setToolTip(data.getToolTip());
                  widgetDef.setXWidgetName(data.getXWidgetName());

                  if (compStateItem != null) {
                     compStateItem.getStateItems().add(widgetDef);
                  } else {
                     stateDef.getStateItems().add(widgetDef);
                  }

                  // Check for end composite
                  boolean endComposite = data.isEndComposite();
                  if (endComposite) {
                     compStateItem = null;
                  }

               } else if (itemDef instanceof WorkRuleDefinition) {
                  WorkRuleDefinition workRule = (WorkRuleDefinition) itemDef;
                  RuleDefinition ruleDef = getRuleById(workRule.getId());
                  stateDef.getRules().add(ruleDef);
               } else {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE,
                     "Unexpected item type as page child -> " + itemDef.getType());
               }
            }
         }
         return workDef;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return null;
   }

   private static StateDefinition getOrCreateState(WorkDefinition workDef, String name) {
      StateDefinition stateDef = workDef.getStateByName(name);
      if (stateDef == null) {
         stateDef = new StateDefinition(name);
         workDef.getStates().add(stateDef);
      }
      return stateDef;
   }

   private static void copyKeyValuePair(AbstractWorkDefItem itemDef, WorkItemDefinition workItem) {
      for (Entry<String, String> entry : workItem.getWorkDataKeyValueMap().entrySet()) {
         itemDef.addWorkDataKeyValue(entry.getKey(), entry.getValue());
      }
   }

   private static WorkDefinition getWorkDefinitionFromArtifactsAttributeValue(Artifact artifact) throws OseeCoreException {
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = artifact.getSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, null);
      if (Strings.isValid(workFlowDefId)) {
         return getWorkDefinition(workFlowDefId);
      }
      return null;
   }

   private static WorkDefinition getWorkDefinitionForTask(TaskArtifact taskArt) throws OseeCoreException {
      WorkDefinition workDef = null;
      for (IAtsTeamWorkflow provider : TeamWorkflowExtensions.getAtsTeamWorkflowExtensions()) {
         String workFlowDefId = provider.getRelatedTaskWorkflowDefinitionId(taskArt.getParentSMA());
         if (Strings.isValid(workFlowDefId)) {
            workDef = WorkDefinitionFactory.getWorkDefinition(workFlowDefId);
            break;
         }
      }
      if (workDef == null) {
         // If task specifies it's own workflow id, use it
         workDef = getWorkDefinitionFromArtifactsAttributeValue(taskArt);
      }
      if (workDef == null) {
         // Else If parent SMA has a related task definition workflow id specified, use it
         workDef = getWorkDefinitionFromArtifactsAttributeValue(taskArt.getParentSMA());
      }
      if (workDef == null) {
         // Else If parent TeamWorkflow's TeamDefinition has a related task definition workflow id, use it
         workDef = getWorkDefinitionFromArtifactsAttributeValue(taskArt.getParentTeamWorkflow().getTeamDefinition());
      }
      if (workDef == null) {
         // Else, use default Task workflow
         workDef = getWorkDefinition(TaskWorkflowDefinition.ID);
      }
      return workDef;
   }

   private static WorkDefinition getWorkDefinitionNew(Artifact artifact) throws OseeCoreException {
      if (artifact instanceof TaskArtifact) {
         return getWorkDefinitionForTask((TaskArtifact) artifact);
      }
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact aba = (AbstractWorkflowArtifact) artifact;
         // Check extensions for definition handling
         for (IAtsTeamWorkflow provider : TeamWorkflowExtensions.getAtsTeamWorkflowExtensions()) {
            String workFlowDefId = provider.getWorkflowDefinitionId(aba);
            if (Strings.isValid(workFlowDefId)) {
               return WorkDefinitionFactory.getWorkDefinition(workFlowDefId);
            }
         }
         // If this artifact specifies it's own workflow definition, use it
         WorkDefinition def = getWorkDefinitionFromArtifactsAttributeValue(artifact);
         if (def != null) {
            return def;
         }
         // Otherwise, use workflow defined by attribute of WorkflowDefinition
         // Note: This is new.  Old TeamDefs got workflow off relation
         if (artifact instanceof TeamWorkFlowArtifact) {
            return getWorkDefinitionFromArtifactsAttributeValue(((TeamWorkFlowArtifact) artifact).getTeamDefinition());
         }
         if (artifact instanceof GoalArtifact) {
            return getWorkDefinition(GoalWorkflowDefinition.ID);
         }
         if (artifact instanceof PeerToPeerReviewArtifact) {
            return getWorkDefinition(PeerToPeerWorkflowDefinition.ID);
         }
         if (artifact instanceof DecisionReviewArtifact) {
            return getWorkDefinition(DecisionWorkflowDefinition.ID);
         }
      }
      return null;
   }

   public static Set<WorkDefinition> getWorkdefinitions() {
      return workDefinitions;
   }

}
