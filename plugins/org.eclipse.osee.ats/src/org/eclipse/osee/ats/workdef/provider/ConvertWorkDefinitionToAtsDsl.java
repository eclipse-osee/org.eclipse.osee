/*
 * Created on Jan 4, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.provider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.AttrWidget;
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.eclipse.osee.ats.dsl.atsDsl.Composite;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef;
import org.eclipse.osee.ats.dsl.atsDsl.FollowupRef;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutDef;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef;
import org.eclipse.osee.ats.dsl.atsDsl.ReviewBlockingType;
import org.eclipse.osee.ats.dsl.atsDsl.StateDef;
import org.eclipse.osee.ats.dsl.atsDsl.ToState;
import org.eclipse.osee.ats.dsl.atsDsl.UserByName;
import org.eclipse.osee.ats.dsl.atsDsl.UserByUserId;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetDef;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetRef;
import org.eclipse.osee.ats.dsl.atsDsl.WorkDef;
import org.eclipse.osee.ats.dsl.atsDsl.WorkflowEventType;
import org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslFactoryImpl;
import org.eclipse.osee.ats.workdef.CompositeStateItem;
import org.eclipse.osee.ats.workdef.DecisionReviewDefinition;
import org.eclipse.osee.ats.workdef.DecisionReviewOption;
import org.eclipse.osee.ats.workdef.PeerReviewDefinition;
import org.eclipse.osee.ats.workdef.RuleDefinition;
import org.eclipse.osee.ats.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.ats.workdef.StateItem;
import org.eclipse.osee.ats.workdef.WidgetDefinition;
import org.eclipse.osee.ats.workdef.WidgetOption;
import org.eclipse.osee.ats.workdef.WorkDefinition;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

public class ConvertWorkDefinitionToAtsDsl {

   private final WorkDefinition workDef;
   private WorkDef dslWorkDef;
   private final XResultData resultData;
   private final Map<String, WidgetDef> idToDslWidgetDefMap = new HashMap<String, WidgetDef>(15);
   private final Map<String, StateDef> nameToDslStateDefMap = new HashMap<String, StateDef>(15);
   private final Map<String, DecisionReviewDef> nameToDslDecisionReviewDefMap = new HashMap<String, DecisionReviewDef>(
      0);
   private final Map<String, PeerReviewDef> nameToDslPeerReviewDefMap = new HashMap<String, PeerReviewDef>(0);

   public ConvertWorkDefinitionToAtsDsl(WorkDefinition workDef, XResultData resultData) {
      this.workDef = workDef;
      this.resultData = resultData;
   }

   public AtsDsl convert(String definitionName) {
      resultData.log("Converting " + workDef.getName() + " to " + definitionName);
      AtsDsl atsDsl = AtsDslFactoryImpl.init().createAtsDsl();

      // Process work definition
      dslWorkDef = AtsDslFactoryImpl.init().createWorkDef();
      dslWorkDef.setName(Strings.quote(definitionName));
      dslWorkDef.getId().add(definitionName);
      if (!workDef.getWorkDataKeyValueMap().isEmpty()) {
         resultData.logError("Unhandled Key/Value for WorkDefinition");
      }
      if (!workDef.getRules().isEmpty()) {
         resultData.logError("Unhandled Rules for WorkDefinition");
      }

      // Process Work States
      for (StateDefinition stateDef : workDef.getStates()) {
         StateDef dslState = AtsDslFactoryImpl.init().createStateDef();
         dslWorkDef.getStates().add(dslState);
         dslState.setName(Strings.quote(stateDef.getName()));
         nameToDslStateDefMap.put(stateDef.getName(), dslState);
         if (Strings.isValid(stateDef.getDescription())) {
            dslState.setDescription(stateDef.getDescription());
         }
         dslState.setOrdinal(stateDef.getOrdinal());
         dslState.setPageType(stateDef.getWorkPageType().name());
         if (workDef.getStartState().getName().equals(stateDef.getName())) {
            dslWorkDef.setStartState(dslState);
         }
         if (!workDef.getWorkDataKeyValueMap().isEmpty()) {
            resultData.logError("Unhandled Key/Value for Work State " + stateDef.getName());
         }

         // Process Work Rules for States
         for (RuleDefinition ruleDef : stateDef.getRules()) {
            String ruleName = ruleDef.getName();
            ruleName = ruleName.replaceAll("^ats", "");
            // If not valid option, need to quote
            try {
               RuleDefinitionOption.valueOf(ruleName);
            } catch (IllegalArgumentException ex) {
               ruleName = Strings.quote(ruleName);
            }
            dslState.getRules().add(ruleName);
         }

         // Process Widgets
         if (!stateDef.getStateItems().isEmpty()) {
            LayoutDef layout = AtsDslFactoryImpl.init().createLayoutDef();
            dslState.setLayout(layout);
            processStateItems(stateDef.getStateItems(), layout, null);
         }

      }

      // Process state referencing DecisionReview References (must be done after states and review definitions processed
      for (StateDefinition stateDef : workDef.getStates()) {
         StateDef dslState = nameToDslStateDefMap.get(stateDef.getName());
         // Process DecisionReviews
         for (DecisionReviewDefinition revDef : stateDef.getDecisionReviews()) {
            DecisionReviewDef dslDecisionRevDef = nameToDslDecisionReviewDefMap.get(revDef.getName());
            if (dslDecisionRevDef == null) {
               dslDecisionRevDef = createDslDecisionReviewDef(revDef);
               dslWorkDef.getDecisionReviewDefs().add(dslDecisionRevDef);
               nameToDslDecisionReviewDefMap.put(revDef.getName(), dslDecisionRevDef);
            }
            DecisionReviewRef dslRevRef = AtsDslFactoryImpl.init().createDecisionReviewRef();
            dslRevRef.setDecisionReview(dslDecisionRevDef);
            dslState.getDecisionReviews().add(dslRevRef);
         }
      }

      // Process state referencing PeerReview References (must be done after states and review definitions processed
      for (StateDefinition stateDef : workDef.getStates()) {
         StateDef dslState = nameToDslStateDefMap.get(stateDef.getName());
         // Process DecisionReviews
         for (PeerReviewDefinition revDef : stateDef.getPeerReviews()) {
            PeerReviewDef dslPeerRevDef = nameToDslPeerReviewDefMap.get(revDef.getName());
            if (dslPeerRevDef == null) {
               dslPeerRevDef = createDslPeerReviewDef(revDef);
               dslWorkDef.getPeerReviewDefs().add(dslPeerRevDef);
               nameToDslPeerReviewDefMap.put(revDef.getName(), dslPeerRevDef);
            }
            PeerReviewRef dslRevRef = AtsDslFactoryImpl.init().createPeerReviewRef();
            dslRevRef.setPeerReview(dslPeerRevDef);
            dslState.getPeerReviews().add(dslRevRef);
         }
      }

      // Process transitions (must do after all states are defined)
      for (StateDefinition stateDef : workDef.getStates()) {
         Set<String> toStateNames = new HashSet<String>();
         StateDef dslStateDef = nameToDslStateDefMap.get(stateDef.getName());
         for (StateDefinition toStateDef : stateDef.getToStates()) {
            // skip states transitioning to themselves
            if (toStateDef.getName().equals(stateDef.getName())) {
               continue;
            }
            // skip duplicate transition to states
            if (toStateNames.contains(toStateDef.getName())) {
               continue;
            } else {
               toStateNames.add(toStateDef.getName());
            }
            ToState dslToState = AtsDslFactoryImpl.init().createToState();
            dslStateDef.getTransitionStates().add(dslToState);
            StateDef dslToStateDef = nameToDslStateDefMap.get(toStateDef.getName());
            dslToState.setState(dslToStateDef);
            if (workDef.getDefaultToState(stateDef) != null && workDef.getDefaultToState(stateDef).getName().equals(
               toStateDef.getName())) {
               dslToState.getOptions().add("AsDefault");
            }
            if (stateDef.getOverrideAttributeValidationStates().contains(toStateDef)) {
               dslToState.getOptions().add("OverrideAttributeValidation");
            }
         }
      }

      resultData.log("Complete");
      atsDsl.setWorkDef(dslWorkDef);
      return atsDsl;
   }

   private DecisionReviewDef createDslDecisionReviewDef(DecisionReviewDefinition revDef) {
      DecisionReviewDef dslRevDef = AtsDslFactoryImpl.init().createDecisionReviewDef();
      dslRevDef.setName(revDef.getName());
      dslRevDef.setBlockingType(ReviewBlockingType.getByName(revDef.getBlockingType().name()));
      dslRevDef.setDescription(revDef.getDescription());
      dslRevDef.setStateEvent(WorkflowEventType.getByName(revDef.getStateEventType().name()));
      StateDef dslStateDef = nameToDslStateDefMap.get(revDef.getRelatedToState());
      dslRevDef.setRelatedToState(dslStateDef);
      dslRevDef.setTitle(revDef.getTitle());
      for (String userId : revDef.getAssignees()) {
         UserByUserId dslUserId = AtsDslFactoryImpl.init().createUserByUserId();
         dslUserId.setUserId(userId);
         dslRevDef.getAssigneeRefs().add(dslUserId);
      }
      if (revDef.isAutoTransitionToDecision()) {
         dslRevDef.setAutoTransitionToDecision(BooleanDef.TRUE);
      }
      for (DecisionReviewOption revOpt : revDef.getOptions()) {
         DecisionReviewOpt dslRevOpt = AtsDslFactoryImpl.init().createDecisionReviewOpt();
         dslRevDef.getOptions().add(dslRevOpt);
         dslRevOpt.setName(revOpt.getName());
         if (revOpt.isFollowupRequired()) {
            FollowupRef dslFollowupRef = AtsDslFactoryImpl.init().createFollowupRef();
            for (String userId : revOpt.getUserIds()) {
               UserByUserId assigneeById = AtsDslFactoryImpl.init().createUserByUserId();
               assigneeById.setUserId(userId);
               dslFollowupRef.getAssigneeRefs().add(assigneeById);
            }
            for (String userName : revOpt.getUserNames()) {
               UserByName assigneeByName = AtsDslFactoryImpl.init().createUserByName();
               assigneeByName.setUserName(userName);
               dslFollowupRef.getAssigneeRefs().add(assigneeByName);
            }
            dslRevOpt.setFollowup(dslFollowupRef);
         }
      }
      return dslRevDef;
   }

   private PeerReviewDef createDslPeerReviewDef(PeerReviewDefinition revDef) {
      PeerReviewDef peerRevDef = AtsDslFactoryImpl.init().createPeerReviewDef();
      peerRevDef.setName(revDef.getName());
      peerRevDef.setBlockingType(ReviewBlockingType.getByName(revDef.getBlockingType().name()));
      peerRevDef.setDescription(revDef.getDescription());
      if (Strings.isValid(revDef.getLocation())) {
         peerRevDef.setLocation(revDef.getLocation());
      }
      if (Strings.isValid(revDef.getTitle())) {
         peerRevDef.setTitle(revDef.getTitle());
      }
      for (String userId : revDef.getAssignees()) {
         UserByUserId dslUserId = AtsDslFactoryImpl.init().createUserByUserId();
         dslUserId.setUserId(userId);
         peerRevDef.getAssigneeRefs().add(dslUserId);
      }
      peerRevDef.setStateEvent(WorkflowEventType.getByName(revDef.getStateEventType().name()));
      StateDef dslStateDef = nameToDslStateDefMap.get(revDef.getRelatedToState());
      peerRevDef.setRelatedToState(dslStateDef);
      return peerRevDef;
   }

   private void processStateItems(List<StateItem> stateItems, LayoutDef layout, Composite dslComposite) {
      for (StateItem stateItem : stateItems) {
         if (stateItem instanceof WidgetDefinition) {

            WidgetDefinition widgetDef = (WidgetDefinition) stateItem;
            AttrWidget attrWidget = getAttrWidget(widgetDef);
            if (attrWidget != null) {
               if (dslComposite != null) {
                  dslComposite.getLayoutItems().add(attrWidget);
               } else {
                  layout.getLayoutItems().add(attrWidget);
               }
            } else {
               WidgetDef dslWidgetDef = getOrCreateWidget(widgetDef);
               WidgetRef dslWidgetRef = AtsDslFactoryImpl.init().createWidgetRef();
               dslWidgetRef.setWidget(dslWidgetDef);
               if (dslComposite != null) {
                  dslComposite.getLayoutItems().add(dslWidgetRef);
               } else {
                  layout.getLayoutItems().add(dslWidgetRef);
               }
            }
         } else if (stateItem instanceof CompositeStateItem) {
            CompositeStateItem composite = (CompositeStateItem) stateItem;
            Composite newDslComposite = AtsDslFactoryImpl.init().createComposite();
            newDslComposite.setNumColumns(composite.getNumColumns());
            if (dslComposite != null) {
               dslComposite.getLayoutItems().add(newDslComposite);
            } else {
               layout.getLayoutItems().add(newDslComposite);
            }
            processStateItems(composite.getStateItems(), layout, newDslComposite);
         } else {
            resultData.logError("Unexpected stateItem => " + stateItem.getName());
         }
      }
   }

   private AttrWidget getAttrWidget(WidgetDefinition widgetDef) {
      List<String> names =
         Arrays.asList("Change Type", "Priority", "Need By", "Estimated Hours", "Work Package", "Validation Required",
            "Estimated Completion Date", "Legacy PCR Id", "Related To State", "LOC Changed", "LOC Reviewed",
            "Pages Changed", "Pages Reviewed", "Goal Order Vote", "Reference Number", "Submitted to Data Management",
            "Submitted to Customer", "Doc Number", "Revision Letter", "CTE SW Release", "Dup CPCR No");
      if (names.contains(widgetDef.getName())) {
         AttrWidget attrWidget = AtsDslFactoryImpl.init().createAttrWidget();
         attrWidget.setAttributeName(widgetDef.getAtrributeName());
         return attrWidget;
      }
      return null;
   }

   private WidgetDef getOrCreateWidget(WidgetDefinition widgetDef) {
      WidgetDef dslWidget = null;
      if (idToDslWidgetDefMap.containsKey(widgetDef.getName())) {
         dslWidget = idToDslWidgetDefMap.get(widgetDef.getName());
      } else {
         dslWidget = AtsDslFactoryImpl.init().createWidgetDef();
         dslWidget.setName(Strings.quote(widgetDef.getName()));
         dslWidget.setDefaultValue(widgetDef.getDefaultValue());
         dslWidget.setDescription(widgetDef.getDescription());
         if (!widgetDef.getWorkDataKeyValueMap().isEmpty()) {
            for (Entry<String, String> entry : widgetDef.getWorkDataKeyValueMap().entrySet()) {
               resultData.logError(String.format("Widget Definition [%s] has unhandled key/value pair [%s][%s]",
                  widgetDef.getName(), entry.getKey(), entry.getValue()));
            }
         }
         for (WidgetOption option : widgetDef.getOptions().getXOptions()) {
            dslWidget.getOption().add(option.name());
         }
         dslWidget.setAttributeName(widgetDef.getAtrributeName());
         dslWidget.setHeight(widgetDef.getHeight());
         dslWidget.setXWidgetName(widgetDef.getXWidgetName());
         idToDslWidgetDefMap.put(widgetDef.getName(), dslWidget);
      }
      dslWorkDef.getWidgetDefs().add(dslWidget);
      return dslWidget;
   }
}
