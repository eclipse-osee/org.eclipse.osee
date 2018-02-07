/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.dsl.integration.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsPeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
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
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ConvertWorkDefinitionToAtsDsl {

   private WorkDef dslWorkDef;
   private final XResultData resultData;
   private final Map<String, WidgetDef> idToDslWidgetDefMap = new HashMap<>(15);
   private final Map<String, StateDef> nameToDslStateDefMap = new HashMap<>(15);
   private final Map<String, DecisionReviewDef> nameToDslDecisionReviewDefMap = new HashMap<>(0);
   private final Map<String, PeerReviewDef> nameToDslPeerReviewDefMap = new HashMap<>(0);

   public ConvertWorkDefinitionToAtsDsl(XResultData resultData) {
      this.resultData = resultData;
   }

   public AtsDsl convert(String definitionName, IAtsWorkDefinition workDef) {
      resultData.log("Converting " + workDef.getName() + " to " + definitionName);
      AtsDsl atsDsl = AtsDslFactoryImpl.init().createAtsDsl();

      // Process work definition
      dslWorkDef = AtsDslFactoryImpl.init().createWorkDef();
      dslWorkDef.setName(Strings.quote(definitionName));
      dslWorkDef.getId().add(workDef.getIdString());

      // Process Work States
      for (IAtsStateDefinition stateDef : workDef.getStates()) {
         StateDef dslState = AtsDslFactoryImpl.init().createStateDef();
         dslWorkDef.getStates().add(dslState);
         dslState.setName(Strings.quote(stateDef.getName()));
         nameToDslStateDefMap.put(stateDef.getName(), dslState);
         if (Strings.isValid(stateDef.getDescription())) {
            dslState.setDescription(stateDef.getDescription());
         }
         dslState.setOrdinal(stateDef.getOrdinal());
         dslState.setPageType(stateDef.getStateType().name());
         if (workDef.getStartState().getName().equals(stateDef.getName())) {
            dslWorkDef.setStartState(dslState);
         }

         // Process Work Rules for States
         List<String> rules = stateDef.getRules();
         processWorkRulesForState(dslState, rules);

         // Process Widgets
         processStateWidgets(stateDef, dslState);

      }

      // Process state referencing DecisionReview References (must be done after states and review definitions processed
      for (IAtsStateDefinition stateDef : workDef.getStates()) {
         StateDef dslState = nameToDslStateDefMap.get(stateDef.getName());
         // Process DecisionReviews
         for (IAtsDecisionReviewDefinition revDef : stateDef.getDecisionReviews()) {
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
      for (IAtsStateDefinition stateDef : workDef.getStates()) {
         StateDef dslState = nameToDslStateDefMap.get(stateDef.getName());
         // Process DecisionReviews
         for (IAtsPeerReviewDefinition revDef : stateDef.getPeerReviews()) {
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
      for (IAtsStateDefinition stateDef : workDef.getStates()) {
         Set<String> toStateNames = new HashSet<>();
         StateDef dslStateDef = nameToDslStateDefMap.get(stateDef.getName());
         for (IAtsStateDefinition toStateDef : stateDef.getToStates()) {
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
            if (stateDef.getDefaultToState() != null && stateDef.getDefaultToState().getName().equals(
               toStateDef.getName())) {
               dslToState.getOptions().add("AsDefault");
            }
            if (stateDef.getOverrideAttributeValidationStates().contains(toStateDef)) {
               dslToState.getOptions().add("OverrideAttributeValidation");
            }
         }
      }

      resultData.log("Complete");
      atsDsl.getWorkDef().add(dslWorkDef);
      return atsDsl;
   }

   protected void processStateWidgets(IAtsStateDefinition stateDef, StateDef dslState) {
      if (!stateDef.getLayoutItems().isEmpty()) {
         LayoutDef layout = AtsDslFactoryImpl.init().createLayoutDef();
         dslState.setLayout(layout);
         processStateItems(stateDef.getLayoutItems(), layout, null);
      }
   }

   protected void processWorkRulesForState(StateDef dslState, List<String> rules) {
      for (String ruleName : rules) {
         ruleName = ruleName.replaceAll("^ats", "");
         // If not valid option, need to quote
         try {
            RuleDefinitionOption.valueOf(ruleName);
         } catch (IllegalArgumentException ex) {
            ruleName = Strings.quote(ruleName);
         }
         dslState.getRules().add(ruleName);
      }
   }

   protected DecisionReviewDef createDslDecisionReviewDef(IAtsDecisionReviewDefinition revDef) {
      DecisionReviewDef dslRevDef = AtsDslFactoryImpl.init().createDecisionReviewDef();
      dslRevDef.setName(revDef.getName());
      dslRevDef.setBlockingType(ReviewBlockingType.getByName(revDef.getBlockingType().name()));
      dslRevDef.setDescription(revDef.getDescription());
      dslRevDef.setStateEvent(WorkflowEventType.getByName(revDef.getStateEventType().name()));
      StateDef dslStateDef = nameToDslStateDefMap.get(revDef.getRelatedToState());
      dslRevDef.setRelatedToState(dslStateDef);
      dslRevDef.setTitle(revDef.getReviewTitle());
      for (String userId : revDef.getAssignees()) {
         UserByUserId dslUserId = AtsDslFactoryImpl.init().createUserByUserId();
         dslUserId.setUserId(userId);
         dslRevDef.getAssigneeRefs().add(dslUserId);
      }
      if (revDef.isAutoTransitionToDecision()) {
         dslRevDef.setAutoTransitionToDecision(BooleanDef.TRUE);
      } else {
         dslRevDef.setAutoTransitionToDecision(BooleanDef.FALSE);
      }
      for (IAtsDecisionReviewOption revOpt : revDef.getOptions()) {
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

   protected PeerReviewDef createDslPeerReviewDef(IAtsPeerReviewDefinition revDef) {
      PeerReviewDef peerRevDef = AtsDslFactoryImpl.init().createPeerReviewDef();
      peerRevDef.setName(revDef.getName());
      peerRevDef.setBlockingType(ReviewBlockingType.getByName(revDef.getBlockingType().name()));
      peerRevDef.setDescription(revDef.getDescription());
      if (Strings.isValid(revDef.getLocation())) {
         peerRevDef.setLocation(revDef.getLocation());
      }
      if (Strings.isValid(revDef.getReviewTitle())) {
         peerRevDef.setTitle(revDef.getReviewTitle());
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

   private void processStateItems(List<IAtsLayoutItem> stateItems, LayoutDef layout, Composite dslComposite) {
      for (IAtsLayoutItem stateItem : stateItems) {
         processStateItem(layout, dslComposite, stateItem);
      }
   }

   protected void processStateItem(LayoutDef layout, Composite dslComposite, IAtsLayoutItem stateItem) {
      if (stateItem instanceof IAtsWidgetDefinition) {
         IAtsWidgetDefinition widgetDef = (IAtsWidgetDefinition) stateItem;
         processWidgetDefinition(layout, dslComposite, widgetDef);
      } else if (stateItem instanceof IAtsCompositeLayoutItem) {
         IAtsCompositeLayoutItem composite = (IAtsCompositeLayoutItem) stateItem;
         processCompositeStateItem(layout, dslComposite, composite);
      } else {
         resultData.error("Unexpected stateItem => " + stateItem.getName());
      }
   }

   protected void processCompositeStateItem(LayoutDef layout, Composite dslComposite, IAtsCompositeLayoutItem composite) {
      Composite newDslComposite = AtsDslFactoryImpl.init().createComposite();
      newDslComposite.setNumColumns(composite.getNumColumns());
      if (dslComposite != null) {
         dslComposite.getLayoutItems().add(newDslComposite);
      } else {
         layout.getLayoutItems().add(newDslComposite);
      }
      processStateItems(composite.getaLayoutItems(), layout, newDslComposite);
   }

   protected void processWidgetDefinition(LayoutDef layout, Composite dslComposite, IAtsWidgetDefinition widgetDef) {
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
   }

   private AttrWidget getAttrWidget(IAtsWidgetDefinition widgetDef) {
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

   protected WidgetDef getOrCreateWidget(IAtsWidgetDefinition widgetDef) {
      WidgetDef dslWidget = null;
      if (idToDslWidgetDefMap.containsKey(widgetDef.getName())) {
         dslWidget = idToDslWidgetDefMap.get(widgetDef.getName());
      } else {
         dslWidget = AtsDslFactoryImpl.init().createWidgetDef();
         dslWidget.setName(Strings.quote(widgetDef.getName()));
         dslWidget.setDefaultValue(widgetDef.getDefaultValue());
         dslWidget.setDescription(widgetDef.getDescription());
         for (WidgetOption option : widgetDef.getOptions().getXOptions()) {
            dslWidget.getOption().add(option.name());
         }
         dslWidget.setAttributeName(widgetDef.getAtrributeName());
         dslWidget.setHeight(widgetDef.getHeight());
         dslWidget.setXWidgetName(widgetDef.getXWidgetName());
         idToDslWidgetDefMap.put(widgetDef.getName(), dslWidget);
      }
      if (dslWorkDef != null) {
         dslWorkDef.getWidgetDefs().add(dslWidget);
      }
      return dslWidget;
   }
}
