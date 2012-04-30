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
package org.eclipse.osee.ats.core.client.workdef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.users.AtsUsers;
import org.eclipse.osee.ats.core.workdef.CompositeStateItem;
import org.eclipse.osee.ats.core.workdef.DecisionReviewDefinition;
import org.eclipse.osee.ats.core.workdef.DecisionReviewOption;
import org.eclipse.osee.ats.core.workdef.PeerReviewDefinition;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.ats.core.workdef.RuleDefinition;
import org.eclipse.osee.ats.core.workdef.StateColor;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.StateEventType;
import org.eclipse.osee.ats.core.workdef.StateItem;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinitionFloatMinMaxConstraint;
import org.eclipse.osee.ats.core.workdef.WidgetDefinitionIntMinMaxConstraint;
import org.eclipse.osee.ats.core.workdef.WidgetDefinitionListMinMaxSelectedConstraint;
import org.eclipse.osee.ats.core.workdef.WidgetOption;
import org.eclipse.osee.ats.core.workdef.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.provider.BooleanDefUtil;
import org.eclipse.osee.ats.core.workdef.provider.UserRefUtil;
import org.eclipse.osee.ats.core.workflow.WorkPageType;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.AttrWidget;
import org.eclipse.osee.ats.dsl.atsDsl.Composite;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef;
import org.eclipse.osee.ats.dsl.atsDsl.FollowupRef;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutCopy;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutDef;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutItem;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutType;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef;
import org.eclipse.osee.ats.dsl.atsDsl.StateDef;
import org.eclipse.osee.ats.dsl.atsDsl.ToState;
import org.eclipse.osee.ats.dsl.atsDsl.UserByName;
import org.eclipse.osee.ats.dsl.atsDsl.UserByUserId;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetDef;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetRef;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.AttributeTypeToXWidgetName;

public class ConvertAtsDslToWorkDefinition {

   private final String name;
   private final AtsDsl atsDsl;

   public ConvertAtsDslToWorkDefinition(String name, AtsDsl atsDsl) {
      this.name = name;
      this.atsDsl = atsDsl;
   }

   public WorkDefinition convert() {
      if (atsDsl.getWorkDef() == null) {
         return null;
      }
      WorkDefinition workDef = new WorkDefinition(Strings.unquote(atsDsl.getWorkDef().getName()));
      for (String id : atsDsl.getWorkDef().getId()) {
         workDef.getIds().add(id);
      }

      List<WidgetDefinition> widgetDefs = retrieveWigetDefs(atsDsl, name);
      Map<StateDefinition, String> copyLayoutFromMap = new HashMap<StateDefinition, String>();

      // Process and define all states
      for (StateDef dslState : atsDsl.getWorkDef().getStates()) {
         String stateName = Strings.unquote(dslState.getName());
         StateDefinition stateDef = workDef.getOrCreateState(stateName);
         stateDef.setWorkDefinition(workDef);
         stateDef.setDescription(dslState.getDescription());

         // Process state settings
         stateDef.setOrdinal(dslState.getOrdinal());
         WorkPageType workPageType = WorkPageType.Working;
         try {
            workPageType = WorkPageType.valueOf(dslState.getPageType());
         } catch (IllegalArgumentException ex) {
            // do nothing
         }
         stateDef.setWorkPageType(workPageType);
         stateDef.setStateWeight(dslState.getPercentWeight());
         stateDef.setRecommendedPercentComplete(dslState.getRecommendedPercentComplete());
         StateColor color = StateColor.BLACK;
         try {
            if (Strings.isValid(dslState.getColor())) {
               color = StateColor.valueOf(dslState.getColor());
            }
         } catch (IllegalArgumentException ex) {
            // do nothing
         }
         stateDef.setColor(color);

         // Process widgets
         LayoutType layout = dslState.getLayout();
         if (layout instanceof LayoutDef) {
            processLayoutItems(name, widgetDefs, stateDef.getStateItems(), ((LayoutDef) layout).getLayoutItems());
         } else if (layout instanceof LayoutCopy) {
            copyLayoutFromMap.put(stateDef, Strings.unquote(((LayoutCopy) layout).getState().getName()));
         }

         // process rules
         for (String ruleName : dslState.getRules()) {
            stateDef.addRule(new RuleDefinition(Strings.unquote(ruleName)), "Dsl StateDef Rule");
         }

      }

      // Process States needing layoutCopy
      for (Entry<StateDefinition, String> entry : copyLayoutFromMap.entrySet()) {
         StateDefinition fromStateDef = workDef.getStateByName(entry.getValue());
         StateDefinition toStateDef = entry.getKey();
         for (StateItem item : fromStateDef.getStateItems()) {
            toStateDef.getStateItems().add(item);
         }
      }

      // Process and define all transitions
      for (StateDef dslState : atsDsl.getWorkDef().getStates()) {
         StateDefinition stateDef = workDef.getStateByName(Strings.unquote(dslState.getName()));
         // Process transitions
         for (ToState dslToState : dslState.getTransitionStates()) {
            StateDefinition toStateDef = workDef.getStateByName(Strings.unquote(dslToState.getState().getName()));
            stateDef.getToStates().add(toStateDef);
            for (String dslTransOption : dslToState.getOptions()) {
               if ("AsDefault".equals(dslTransOption)) {
                  stateDef.setDefaultToState(toStateDef);
               }
               if ("OverrideAttributeValidation".equals(dslTransOption)) {
                  stateDef.getOverrideAttributeValidationStates().add(toStateDef);
               }
            }
         }
      }

      // Process all decision reviews
      for (StateDef dslState : atsDsl.getWorkDef().getStates()) {
         StateDefinition stateDef = workDef.getStateByName(Strings.unquote(dslState.getName()));
         for (DecisionReviewRef dslRevRef : dslState.getDecisionReviews()) {
            DecisionReviewDef dslRevDef = dslRevRef.getDecisionReview();
            DecisionReviewDefinition revDef = convertDslDecisionReview(dslRevDef);
            if (!Strings.isValid(revDef.getRelatedToState())) {
               revDef.setRelatedToState(stateDef.getName());
            }
            stateDef.getDecisionReviews().add(revDef);
         }
      }

      // Process all peer reviews
      for (StateDef dslState : atsDsl.getWorkDef().getStates()) {
         StateDefinition stateDef = workDef.getStateByName(Strings.unquote(dslState.getName()));
         for (PeerReviewRef peerRevRef : dslState.getPeerReviews()) {
            PeerReviewDef dslRevDef = peerRevRef.getPeerReview();
            PeerReviewDefinition revDef = convertDslPeerReview(dslRevDef);
            if (!Strings.isValid(revDef.getRelatedToState())) {
               revDef.setRelatedToState(stateDef.getName());
            }
            stateDef.getPeerReviews().add(revDef);
         }
      }

      // Set the start state
      workDef.setStartState(workDef.getStateByName(Strings.unquote(atsDsl.getWorkDef().getStartState().getName())));

      return workDef;
   }

   private DecisionReviewDefinition convertDslDecisionReview(DecisionReviewDef dslRevDef) {
      DecisionReviewDefinition revDef = new DecisionReviewDefinition(dslRevDef.getName());
      revDef.setReviewTitle(dslRevDef.getTitle());
      revDef.setDescription(dslRevDef.getDescription());

      String dslBlockType = dslRevDef.getBlockingType().getName();
      ReviewBlockType blockType = ReviewBlockType.None;
      try {
         blockType = ReviewBlockType.valueOf(dslBlockType);
      } catch (IllegalArgumentException ex) {
         OseeLog.logf(Activator.class, Level.WARNING, "Unknown ReviewBlockType [%s]; Defaulting to None", dslBlockType);
      }
      revDef.setBlockingType(blockType);

      String dslEventType = dslRevDef.getStateEvent().getName();
      StateEventType eventType = StateEventType.None;
      try {
         eventType = StateEventType.valueOf(dslEventType);
      } catch (IllegalArgumentException ex) {
         OseeLog.logf(Activator.class, Level.WARNING, "Unknown StateEventType [%s]; Defaulting to None", dslEventType);
      }
      revDef.setStateEventType(eventType);
      revDef.setAutoTransitionToDecision(BooleanDefUtil.get(dslRevDef.getAutoTransitionToDecision(), false));

      for (DecisionReviewOpt dslOpt : dslRevDef.getOptions()) {
         DecisionReviewOption revOpt = new DecisionReviewOption(Strings.unquote(dslOpt.getName()));
         FollowupRef followupRef = dslOpt.getFollowup();
         if (followupRef == null) {
            revOpt.setFollowupRequired(false);
         } else {
            revOpt.getUserIds().addAll(UserRefUtil.getUserIds(followupRef.getAssigneeRefs()));
            revOpt.getUserNames().addAll(UserRefUtil.getUserNames(followupRef.getAssigneeRefs()));
         }
         revDef.getOptions().add(revOpt);
      }

      Collection<String> userIds = getAssigneesFromUserRefs(dslRevDef.getAssigneeRefs());
      revDef.getAssignees().addAll(userIds);
      return revDef;
   }

   private void processLayoutItems(String SHEET_NAME, List<WidgetDefinition> widgetDefs, List<StateItem> stateItems, EList<LayoutItem> layoutItems) {
      for (LayoutItem layoutItem : layoutItems) {
         if (layoutItem instanceof WidgetDef) {
            WidgetDefinition widgetDef = convertDslWidgetDef((WidgetDef) layoutItem, SHEET_NAME);
            stateItems.add(widgetDef);
         } else if (layoutItem instanceof WidgetRef) {
            String widgetName = Strings.unquote(((WidgetRef) layoutItem).getWidget().getName());
            boolean found = false;
            for (WidgetDefinition wd : widgetDefs) {
               if (wd.getName().equals(widgetName)) {
                  stateItems.add(wd);
                  found = true;
               }
            }
            if (!found) {
               OseeLog.logf(Activator.class, Level.SEVERE, "Could not find WidgetRef [%s] in WidgetDefs", widgetName);
            }
         } else if (layoutItem instanceof AttrWidget) {
            AttrWidget attrWidget = (AttrWidget) layoutItem;
            String attributeName = attrWidget.getAttributeName();
            try {
               AttributeType attributeType = AttributeTypeManager.getType(attributeName);
               if (attributeType == null) {
                  OseeLog.logf(Activator.class, Level.SEVERE, "Invalid attribute name [%s] in WorkDefinition [%s]",
                     attributeName, SHEET_NAME);
               } else {
                  WidgetDefinition widgetDef = new WidgetDefinition(attributeType.getUnqualifiedName());
                  widgetDef.setAttributeName(attributeType.getName());
                  setXWidgetNameBasedOnAttribute(attributeType, widgetDef);
                  extractDslWidgetDefOptions(attrWidget.getOption(), SHEET_NAME, widgetDef);
                  stateItems.add(widgetDef);
               }
            } catch (Exception ex) {
               OseeLog.logf(Activator.class, Level.SEVERE, ex,
                  "Error resolving attribute [%s] to WorkDefinition in [%s]", attributeName, SHEET_NAME);
            }
         } else if (layoutItem instanceof Composite) {
            Composite composite = (Composite) layoutItem;
            CompositeStateItem compStateItem = new CompositeStateItem(composite.getNumColumns());
            if (!composite.getLayoutItems().isEmpty()) {
               processLayoutItems(SHEET_NAME, widgetDefs, compStateItem.getStateItems(), composite.getLayoutItems());
            }
            stateItems.add(compStateItem);
         }
      }

   }

   private PeerReviewDefinition convertDslPeerReview(PeerReviewDef dslRevDef) {
      PeerReviewDefinition revDef = new PeerReviewDefinition(dslRevDef.getName());
      revDef.setReviewTitle(dslRevDef.getTitle());
      revDef.setDescription(dslRevDef.getDescription());
      revDef.setLocation(dslRevDef.getLocation());

      String dslBlockType = dslRevDef.getBlockingType().getName();
      ReviewBlockType blockType = ReviewBlockType.None;
      try {
         blockType = ReviewBlockType.valueOf(dslBlockType);
      } catch (IllegalArgumentException ex) {
         OseeLog.logf(Activator.class, Level.WARNING, "Unknown ReviewBlockType [%s]; Defaulting to None", dslBlockType);
      }
      revDef.setBlockingType(blockType);

      String dslEventType = dslRevDef.getStateEvent().getName();
      StateEventType eventType = StateEventType.None;
      try {
         eventType = StateEventType.valueOf(dslEventType);
      } catch (IllegalArgumentException ex) {
         OseeLog.logf(Activator.class, Level.WARNING, "Unknown StateEventType [%s]; Defaulting to None", dslEventType);
      }
      revDef.setStateEventType(eventType);
      Collection<String> userIds = getAssigneesFromUserRefs(dslRevDef.getAssigneeRefs());
      revDef.getAssignees().addAll(userIds);
      return revDef;
   }

   private Collection<String> getAssigneesFromUserRefs(EList<UserRef> UserRefs) {
      Set<String> userIds = new HashSet<String>();
      for (UserRef UserRef : UserRefs) {
         if (UserRef instanceof UserByName) {
            UserByName byName = (UserByName) UserRef;
            String name = Strings.unquote(byName.getUserName());
            if (!Strings.isValid(name)) {
               OseeLog.logf(Activator.class, Level.WARNING, "Unhandled UserByName name [%s]", name);
               continue;
            }
            try {
               IAtsUser user = AtsUsersClient.getUserByName(name);
               userIds.add(user.getUserId());
            } catch (OseeCoreException ex) {
               OseeLog.logf(Activator.class, Level.WARNING, "No user by name [%s] [%s]", name, ex.getLocalizedMessage());
            }
         } else if (UserRef instanceof UserByUserId) {
            UserByUserId byUserId = (UserByUserId) UserRef;
            String userId = byUserId.getUserId();
            if (!Strings.isValid(userId)) {
               OseeLog.logf(Activator.class, Level.WARNING, "Unhandled UserByUserId id [%s]", userId);
               continue;
            }
            try {
               IAtsUser user = AtsUsers.getUser(userId);
               userIds.add(user.getUserId());
            } catch (OseeCoreException ex) {
               OseeLog.logf(Activator.class, Level.WARNING, "No user by id [%s] [%s]", userId, ex.getLocalizedMessage());
            }
         } else {
            OseeLog.logf(Activator.class, Level.WARNING, "Unhandled UserRef type [%s]", UserRef);
         }
      }
      return userIds;
   }

   private void setXWidgetNameBasedOnAttribute(AttributeType attributeType, WidgetDefinition widgetDef) throws OseeCoreException {
      if (!Strings.isValid(widgetDef.getXWidgetName())) {
         widgetDef.setXWidgetName(AttributeTypeToXWidgetName.getXWidgetName(attributeType));
      }
   }

   private List<WidgetDefinition> retrieveWigetDefs(AtsDsl atsDsl, String SHEET_NAME) {
      List<WidgetDefinition> widgetDefs = new ArrayList<WidgetDefinition>();
      for (WidgetDef dslWidgetDef : atsDsl.getWorkDef().getWidgetDefs()) {
         WidgetDefinition widgetDef = convertDslWidgetDef(dslWidgetDef, SHEET_NAME);
         widgetDefs.add(widgetDef);
      }
      return widgetDefs;
   }

   private WidgetDefinition convertDslWidgetDef(WidgetDef dslWidgetDef, String SHEET_NAME) {
      WidgetDefinition widgetDef = new WidgetDefinition(Strings.unquote(dslWidgetDef.getName()));
      widgetDef.setAttributeName(dslWidgetDef.getAttributeName());
      // Set description if model defines it
      if (Strings.isValid(dslWidgetDef.getDescription())) {
         widgetDef.setDescription(dslWidgetDef.getDescription());
      }
      // Else, set if AtsAttributeTypes defines it
      else if (Strings.isValid(dslWidgetDef.getAttributeName()) && AtsAttributeTypes.getTypeByName(dslWidgetDef.getAttributeName()) != null && Strings.isValid(AtsAttributeTypes.getTypeByName(
         dslWidgetDef.getAttributeName()).getDescription())) {
         widgetDef.setDescription(AtsAttributeTypes.getTypeByName(dslWidgetDef.getAttributeName()).getDescription());
      }
      if (Strings.isValid(dslWidgetDef.getXWidgetName())) {
         widgetDef.setXWidgetName(dslWidgetDef.getXWidgetName());
      } else {
         String attributeName = dslWidgetDef.getAttributeName();
         if (Strings.isValid(attributeName)) {
            try {
               AttributeType attributeType = AttributeTypeManager.getType(attributeName);
               if (attributeType == null) {
                  OseeLog.logf(Activator.class, Level.SEVERE, "Invalid attribute name [%s] in WorkDefinition [%s]",
                     attributeName, SHEET_NAME);
               } else {
                  setXWidgetNameBasedOnAttribute(attributeType, widgetDef);
               }
            } catch (OseeCoreException ex) {
               OseeLog.logf(Activator.class, Level.SEVERE,
                  "Error resolving attribute name [%s] in WorkDefinition [%s]", attributeName, SHEET_NAME);
            }
         } else {
            OseeLog.logf(Activator.class, Level.SEVERE, "Invalid attribute name [%s] in WorkDefinition [%s]",
               attributeName, SHEET_NAME);
         }
      }
      processMinMaxConstraints(widgetDef, dslWidgetDef.getMinConstraint(), dslWidgetDef.getMaxConstraint());

      widgetDef.setHeight(dslWidgetDef.getHeight());
      widgetDef.setDefaultValue(dslWidgetDef.getDefaultValue());
      extractDslWidgetDefOptions(dslWidgetDef.getOption(), SHEET_NAME, widgetDef);
      return widgetDef;
   }

   private void processMinMaxConstraints(WidgetDefinition widgetDef, String minConstraint, String maxConstraint) {
      if (!Strings.isValid(minConstraint) && !Strings.isValid(maxConstraint)) {
         return;
      }
      if (widgetDef.getXWidgetName().contains("Float")) {
         widgetDef.getConstraints().add(new WidgetDefinitionFloatMinMaxConstraint(minConstraint, minConstraint));
      }
      if (widgetDef.getXWidgetName().contains("Integer")) {
         widgetDef.getConstraints().add(new WidgetDefinitionIntMinMaxConstraint(minConstraint, minConstraint));
      }
      if (widgetDef.getXWidgetName().contains("List")) {
         widgetDef.getConstraints().add(new WidgetDefinitionListMinMaxSelectedConstraint(minConstraint, minConstraint));
      }
   }

   private void extractDslWidgetDefOptions(EList<String> options, String SHEET_NAME, WidgetDefinition widgetDef) {
      for (String value : options) {
         WidgetOption option = null;
         try {
            option = WidgetOption.valueOf(value);
            widgetDef.getOptions().add(option);
         } catch (IllegalArgumentException ex) {
            OseeLog.logf(Activator.class, Level.WARNING, ex, "Unexpected value [%s] in WorkDefinition [%s] ", value,
               SHEET_NAME);
         }
      }
   }

}
