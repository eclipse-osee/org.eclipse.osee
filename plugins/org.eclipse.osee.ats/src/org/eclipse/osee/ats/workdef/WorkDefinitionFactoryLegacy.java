/*
 * Created on May 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.review.DecisionOption;
import org.eclipse.osee.ats.core.review.XDecisionOptions;
import org.eclipse.osee.ats.core.workdef.AbstractWorkDefItem;
import org.eclipse.osee.ats.core.workdef.CompositeStateItem;
import org.eclipse.osee.ats.core.workdef.DecisionReviewDefinition;
import org.eclipse.osee.ats.core.workdef.DecisionReviewOption;
import org.eclipse.osee.ats.core.workdef.IWorkDefintionFactoryLegacyMgr;
import org.eclipse.osee.ats.core.workdef.PeerReviewDefinition;
import org.eclipse.osee.ats.core.workdef.RuleDefinition;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetOption;
import org.eclipse.osee.ats.core.workdef.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionMatch;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.workflow.item.AtsAddDecisionReviewRule;
import org.eclipse.osee.ats.workflow.item.AtsAddPeerToPeerReviewRule;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinitionMatch;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionFactoryLegacy implements IWorkDefintionFactoryLegacyMgr {
   private static String AtsStatePercentCompleteWeightRule = "atsStatePercentCompleteWeight";
   private static final Map<String, RuleDefinition> idToRule = new HashMap<String, RuleDefinition>();

   public static RuleDefinition getRuleById(String id) {
      ensureRulesLoaded();
      return idToRule.get(id);
   }

   public static void clearCaches() {
      idToRule.clear();
   }

   private synchronized static void ensureRulesLoaded() {
      if (idToRule.isEmpty()) {
         try {
            for (WorkItemDefinition workItem : WorkItemDefinitionFactory.getWorkItemDefinitions()) {
               if (workItem instanceof WorkRuleDefinition) {
                  if (workItem.getName().startsWith("atsAddDecisionReview")) {
                     System.err.println("skipping rule " + workItem.getName());
                  } else if (workItem.getName().startsWith("atsAddPeerToPeerReview")) {
                     System.err.println("skipping rule " + workItem.getName());
                  } else if (workItem.getName().startsWith(AtsStatePercentCompleteWeightRule)) {
                     System.err.println("skipping rule " + workItem.getName());
                  } else {
                     try {
                        WorkRuleDefinition workRule = (WorkRuleDefinition) workItem;
                        // All rules in DB should map to RuleDefinitionOption 
                        RuleDefinition ruleDef = null;
                        String workRuleName = workRule.getName().replaceFirst("^ats", "");
                        try {
                           RuleDefinitionOption ruleOption = RuleDefinitionOption.valueOf(workRuleName);
                           ruleDef = new RuleDefinition(ruleOption);
                        } catch (IllegalArgumentException ex) {
                           ruleDef = new RuleDefinition(workRuleName);
                        }
                        ruleDef.setDescription(workRule.getDescription());
                        copyKeyValuePair(ruleDef, workRule);
                        idToRule.put(ruleDef.getName(), ruleDef);
                     } catch (Exception ex) {
                        OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                     }
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   public static WorkDefinition translateToWorkDefinition(WorkFlowDefinition workFlowDef) {
      ensureRulesLoaded();
      try {
         String startWorkPageName = workFlowDef.getResolvedStartPageId();
         WorkDefinition workDef = new WorkDefinition(workFlowDef.getId());
         addRulesToWorkDefinition(workFlowDef, workDef);
         for (WorkPageDefinition workPage : workFlowDef.getPages()) {
            // not using ids anymore for states, widgets or rules
            StateDefinition stateDef = workDef.getOrCreateState(workPage.getPageName());
            stateDef.setWorkDefinition(workDef);
            stateDef.setOrdinal(workPage.getWorkPageOrdinal());
            if (workPage.getId().equals(startWorkPageName)) {
               workDef.setStartState(stateDef);
            }
            // TODO get rid of this??
            copyKeyValuePair(stateDef, workPage);
            stateDef.setWorkPageType(workPage.getWorkPageType());

            for (WorkPageDefinition returnPageDefinition : workFlowDef.getReturnPages(workPage)) {
               // new model doesn't have AsReturn, instead it uses OverrideAttributeValidation
               StateDefinition returnStateDef = workDef.getOrCreateState(returnPageDefinition.getPageName());
               stateDef.getOverrideAttributeValidationStates().add(returnStateDef);
            }
            for (WorkPageDefinition toPageDefinition : workFlowDef.getToPages(workPage)) {
               StateDefinition toStateDef = workDef.getOrCreateState(toPageDefinition.getPageName());
               stateDef.getToStates().add(toStateDef);
            }
            WorkPageDefinition defaultToPageDefinition = workFlowDef.getDefaultToPage(workPage);
            if (defaultToPageDefinition != null) {
               StateDefinition defaultToStateDef = workDef.getOrCreateState(defaultToPageDefinition.getPageName());
               stateDef.setDefaultToState(defaultToStateDef);
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
                  widgetDef.setAttributeName(data.getStoreName());
                  widgetDef.setDefaultValue(data.getDefaultValue());
                  if (data.isHeightSet()) {
                     widgetDef.setHeight(data.getHeight());
                  }
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
                  if (workRule.getName().startsWith("atsAddDecisionReview")) {
                     DecisionReviewDefinition decRevDef = convertDecisionReviewRule(workRule);
                     if (!Strings.isValid(decRevDef.getRelatedToState())) {
                        decRevDef.setRelatedToState(stateDef.getName());
                     }
                     stateDef.getDecisionReviews().add(decRevDef);
                  } else if (workRule.getName().startsWith("atsAddPeerToPeerReview")) {
                     PeerReviewDefinition peerRevDef = convertPeerReviewRule(workRule);
                     if (!Strings.isValid(peerRevDef.getRelatedToState())) {
                        peerRevDef.setRelatedToState(stateDef.getName());
                     }
                     stateDef.getPeerReviews().add(peerRevDef);
                  } else {
                     RuleDefinition ruleDef = getRuleById(workRule.getId().replaceFirst("^ats", ""));
                     if (ruleDef == null) {
                        OseeLog.log(AtsPlugin.class, Level.SEVERE,
                           String.format("Null work rule for " + workRule.getId()));
                     } else {
                        stateDef.addRule(ruleDef, "from related WorkItemDefintion");
                     }
                  }
               } else {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE,
                     "Unexpected item type as page child -> " + itemDef.getType());
               }
            }
         }

         // Process WeightDefinitions
         Artifact workDefArt = workFlowDef.getArtifact();
         for (Artifact workChild : workDefArt.getRelatedArtifacts(CoreRelationTypes.WorkItem__Child)) {
            if (workChild.getName().startsWith(AtsStatePercentCompleteWeightRule)) {
               WorkRuleDefinition ruleDefinition = new WorkRuleDefinition(workChild);
               for (String stateName : ruleDefinition.getWorkDataKeyValueMap().keySet()) {
                  String value = ruleDefinition.getWorkDataValue(stateName);
                  try {
                     double percent = new Double(value).doubleValue();
                     if (percent < 0.0 || percent > 1) {
                        OseeLog.log(
                           AtsPlugin.class,
                           Level.SEVERE,
                           "Invalid percent value \"" + value + "\" (must be 0..1) for rule " + ruleDefinition.getName(),
                           new OseeArgumentException("state map exception"));
                     } else {
                        percent = percent * 100;
                        workDef.getStateByName(stateName).setPercentWeight((int) percent);
                     }
                  } catch (Exception ex) {
                     OseeLog.log(
                        AtsPlugin.class,
                        Level.SEVERE,
                        "Invalid percent value \"" + value + "\" (must be float 0..1) for rule " + ruleDefinition.getName(),
                        new OseeArgumentException("state map exception"));
                  }
               }
               IStatus result = workDef.validateStateWeighting();
               if (!result.isOK()) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE,
                     "Error translating weight definitions - " + result.getMessage());
               }
            }
         }
         return workDef;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return null;
   }

   private static void copyKeyValuePair(AbstractWorkDefItem itemDef, WorkItemDefinition workItem) {
      for (Entry<String, String> entry : workItem.getWorkDataKeyValueMap().entrySet()) {
         // XWidget is a key of WorkWidgetDefinition and should be handled through the normal conversion process
         if (!entry.getKey().equals("XWidget")) {
            itemDef.addWorkDataKeyValue(entry.getKey(), entry.getValue());
         }
      }

   }

   private static DecisionReviewDefinition convertDecisionReviewRule(WorkRuleDefinition workRule) throws OseeCoreException {
      DecisionReviewDefinition revDef = new DecisionReviewDefinition(workRule.getName());
      revDef.setBlockingType(AtsAddDecisionReviewRule.getReviewBlockTypeOrDefault(workRule));
      for (User user : AtsAddDecisionReviewRule.getAssigneesOrDefault(workRule)) {
         revDef.getAssignees().add(user.getUserId());
      }
      revDef.setReviewTitle(AtsAddDecisionReviewRule.getReviewTitle(workRule));
      revDef.setRelatedToState(AtsAddDecisionReviewRule.getRelatedToState(workRule));
      revDef.setDescription(workRule.getDescription());
      revDef.setAutoTransitionToDecision(true);
      revDef.setStateEventType(AtsAddDecisionReviewRule.getStateEventType(workRule));
      for (DecisionOption decOpt : XDecisionOptions.getDecisionOptions(AtsAddDecisionReviewRule.getDecisionOptionString(workRule))) {
         DecisionReviewOption revOpt = new DecisionReviewOption(decOpt.getName());
         revOpt.setFollowupRequired(decOpt.isFollowupRequired());
         for (User user : decOpt.getAssignees()) {
            revOpt.getUserIds().add(user.getUserId());
         }
         revDef.getOptions().add(revOpt);
      }
      return revDef;
   }

   private static PeerReviewDefinition convertPeerReviewRule(WorkRuleDefinition workRule) throws OseeCoreException {
      PeerReviewDefinition revDef = new PeerReviewDefinition(workRule.getName());
      revDef.setBlockingType(AtsAddPeerToPeerReviewRule.getReviewBlockTypeOrDefault(workRule));
      for (User user : AtsAddPeerToPeerReviewRule.getAssigneesOrDefault(workRule)) {
         revDef.getAssignees().add(user.getUserId());
      }
      revDef.setReviewTitle(AtsAddPeerToPeerReviewRule.getReviewTitle(workRule));
      revDef.setRelatedToState(AtsAddPeerToPeerReviewRule.getRelatedToState(workRule));
      revDef.setDescription(workRule.getDescription());
      revDef.setLocation(AtsAddPeerToPeerReviewRule.getLocation(workRule));
      revDef.setStateEventType(AtsAddPeerToPeerReviewRule.getStateEventType(workRule));
      return revDef;
   }

   private static void addRulesToWorkDefinition(WorkFlowDefinition workFlowDef, WorkDefinition workDef) {
      try {
         for (WorkRuleDefinition workRule : workFlowDef.getWorkRules()) {
            RuleDefinition ruleDef = getRuleById(workRule.getId());
            workDef.getRules().add(ruleDef);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE,
            "Error processing WorkRuleDefinition for workflow " + workDef.getName(), ex);
      }
   }

   @Override
   public WorkDefinitionMatch getWorkFlowDefinitionFromId(String id) throws OseeCoreException {
      WorkDefinitionMatch match = new WorkDefinitionMatch();
      WorkFlowDefinition workFlowDef = (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(id);
      if (workFlowDef != null) {
         WorkDefinition workDef = translateToWorkDefinition(workFlowDef);
         if (workDef != null) {
            match.setWorkDefinition(workDef);
            match.addTrace(String.format("from legacy WorkFlowDefinition [%s] translated for id [%s]", id, id));
         }
      }
      return match;
   }

   @Override
   public WorkDefinitionMatch getWorkFlowDefinitionFromReverseId(String id) throws OseeCoreException {
      WorkDefinitionMatch match = new WorkDefinitionMatch();
      String reverseId = AtsWorkDefinitionSheetProviders.getReverseOverrideId(id);
      if (reverseId != null) {
         WorkFlowDefinition workFlowDef =
            (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(reverseId);
         if (workFlowDef != null) {
            WorkDefinition workDef = translateToWorkDefinition(workFlowDef);
            if (workDef != null) {
               match.setWorkDefinition(workDef);
               match.addTrace(String.format(
                  "from legacy WorkFlowDefinition [%s] translated for id [%s] falling back to reverse id [%s]", id, id,
                  reverseId));
            }
         }
      }
      return match;
   }

   @Override
   public WorkDefinitionMatch getWorkFlowDefinitionFromArtifact(Artifact artifact) throws OseeCoreException {
      WorkDefinitionMatch match = new WorkDefinitionMatch();
      WorkFlowDefinitionMatch flowMatch = WorkFlowDefinitionFactory.getWorkFlowDefinition(artifact);
      if (flowMatch.isMatched()) {
         WorkDefinition workDef = translateToWorkDefinition(flowMatch.getWorkFlowDefinition());
         match = new WorkDefinitionMatch(workDef, null);
         match.getTrace().addAll(flowMatch.getTrace());
      }
      return match;
   }

   @Override
   public String getOverrideId(String legacyId) {
      return AtsWorkDefinitionSheetProviders.getOverrideId(legacyId);
   }

   @Override
   public WorkDefinitionMatch getWorkFlowDefinitionFromTeamDefition(TeamDefinitionArtifact teamDefinition) {
      // TODO not sure what to put here
      return null;
   }
}
