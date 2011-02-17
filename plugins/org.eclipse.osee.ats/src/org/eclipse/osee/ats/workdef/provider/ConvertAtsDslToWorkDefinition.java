/*
 * Created on Feb 15, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.provider;

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
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.workdef.CompositeStateItem;
import org.eclipse.osee.ats.workdef.DecisionReviewDefinition;
import org.eclipse.osee.ats.workdef.DecisionReviewOption;
import org.eclipse.osee.ats.workdef.PeerReviewDefinition;
import org.eclipse.osee.ats.workdef.ReviewBlockType;
import org.eclipse.osee.ats.workdef.RuleDefinition;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.ats.workdef.StateEventType;
import org.eclipse.osee.ats.workdef.StateItem;
import org.eclipse.osee.ats.workdef.WidgetDefinition;
import org.eclipse.osee.ats.workdef.WidgetOption;
import org.eclipse.osee.ats.workdef.WorkDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.AttributeXWidgetManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

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

         // Process state settings
         stateDef.setOrdinal(dslState.getOrdinal());
         WorkPageType workPageType = WorkPageType.Working;
         try {
            workPageType = WorkPageType.valueOf(dslState.getPageType());
         } catch (IllegalArgumentException ex) {
            // do nothing
         }
         stateDef.setWorkPageType(workPageType);
         stateDef.setPercentWeight(dslState.getPercentWeight());

         // Process widgets
         LayoutType layout = dslState.getLayout();
         if (layout instanceof LayoutDef) {
            processLayoutItems(name, widgetDefs, stateDef.getStateItems(), ((LayoutDef) layout).getLayoutItems());
         } else if (layout instanceof LayoutCopy) {
            copyLayoutFromMap.put(stateDef, Strings.unquote(((LayoutCopy) layout).getState().getName()));
         }

         // process rules
         for (String ruleName : dslState.getRules()) {
            stateDef.addRule(new RuleDefinition(ruleName), "Dsl StateDef Rule");
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
         OseeLog.log(AtsPlugin.class, Level.WARNING,
            String.format("Unknown ReviewBlockType [%s]; Defaulting to None", dslBlockType));
      }
      revDef.setBlockingType(blockType);

      String dslEventType = dslRevDef.getStateEvent().getName();
      StateEventType eventType = StateEventType.None;
      try {
         eventType = StateEventType.valueOf(dslEventType);
      } catch (IllegalArgumentException ex) {
         OseeLog.log(AtsPlugin.class, Level.WARNING,
            String.format("Unknown StateEventType [%s]; Defaulting to None", dslEventType));
      }
      revDef.setStateEventType(eventType);
      revDef.setAutoTransitionToDecision(BooleanDefUtil.get(dslRevDef.getAutoTransitionToDecision(), false));

      for (DecisionReviewOpt dslOpt : dslRevDef.getOptions()) {
         DecisionReviewOption revOpt = new DecisionReviewOption(dslOpt.getName());
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
               OseeLog.log(AtsPlugin.class, Level.SEVERE,
                  String.format("Could not find WidgetRef [%s] in WidgetDefs", widgetName));
            }
         } else if (layoutItem instanceof AttrWidget) {
            AttrWidget attrWidget = (AttrWidget) layoutItem;
            String attributeName = attrWidget.getAttributeName();
            try {
               AttributeType attributeType = AttributeTypeManager.getType(attributeName);
               if (attributeType == null) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE,
                     String.format("Invalid attribute name [%s] in WorkDefinition [%s]", attributeName, SHEET_NAME));
               } else {
                  WidgetDefinition widgetDef = new WidgetDefinition(attributeType.getUnqualifiedName());
                  widgetDef.setAttributeName(attributeType.getName());

                  setXWidgetNameBasedOnAttribute(attributeType, widgetDef);
                  extractDslWidgetDefOptions(attrWidget.getOption(), SHEET_NAME, widgetDef);
                  stateItems.add(widgetDef);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex,
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
         OseeLog.log(AtsPlugin.class, Level.WARNING,
            String.format("Unknown ReviewBlockType [%s]; Defaulting to None", dslBlockType));
      }
      revDef.setBlockingType(blockType);

      String dslEventType = dslRevDef.getStateEvent().getName();
      StateEventType eventType = StateEventType.None;
      try {
         eventType = StateEventType.valueOf(dslEventType);
      } catch (IllegalArgumentException ex) {
         OseeLog.log(AtsPlugin.class, Level.WARNING,
            String.format("Unknown StateEventType [%s]; Defaulting to None", dslEventType));
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
               OseeLog.log(AtsPlugin.class, Level.WARNING, String.format("Unhandled UserByName name [%s]", name));
               continue;
            }
            try {
               User user = UserManager.getUserByName(name);
               userIds.add(user.getUserId());
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.WARNING,
                  String.format("No user by name [%s] [%s]", name, ex.getLocalizedMessage()));
            }
         } else if (UserRef instanceof UserByUserId) {
            UserByUserId byUserId = (UserByUserId) UserRef;
            String userId = byUserId.getUserId();
            if (!Strings.isValid(userId)) {
               OseeLog.log(AtsPlugin.class, Level.WARNING, String.format("Unhandled UserByUserId id [%s]", userId));
               continue;
            }
            try {
               User user = UserManager.getUserByUserId(userId);
               userIds.add(user.getUserId());
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.WARNING,
                  String.format("No user by id [%s] [%s]", userId, ex.getLocalizedMessage()));
            }
         } else {
            OseeLog.log(AtsPlugin.class, Level.WARNING, String.format("Unhandled UserRef type [%s]", UserRef));
         }
      }
      return userIds;
   }

   private void setXWidgetNameBasedOnAttribute(AttributeType attributeType, WidgetDefinition widgetDef) throws OseeCoreException {
      IAttributeXWidgetProvider xWidgetProvider = AttributeXWidgetManager.getAttributeXWidgetProvider(attributeType);
      List<DynamicXWidgetLayoutData> concreteWidgets = xWidgetProvider.getDynamicXWidgetLayoutData(attributeType);
      widgetDef.setXWidgetName(concreteWidgets.iterator().next().getXWidgetName());
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
      widgetDef.setDescription(dslWidgetDef.getDescription());
      if (Strings.isValid(dslWidgetDef.getXWidgetName())) {
         widgetDef.setXWidgetName(dslWidgetDef.getXWidgetName());
      } else {
         String attributeName = dslWidgetDef.getAttributeName();
         if (Strings.isValid(attributeName)) {
            try {
               AttributeType attributeType = AttributeTypeManager.getType(attributeName);
               if (attributeType == null) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE,
                     String.format("Invalid attribute name [%s] in WorkDefinition [%s]", attributeName, SHEET_NAME));
               } else {
                  setXWidgetNameBasedOnAttribute(attributeType, widgetDef);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(
                  AtsPlugin.class,
                  Level.SEVERE,
                  String.format("Error resolving attribute name [%s] in WorkDefinition [%s]", attributeName, SHEET_NAME));
            }
         } else {
            OseeLog.log(AtsPlugin.class, Level.SEVERE,
               String.format("Invalid attribute name [%s] in WorkDefinition [%s]", attributeName, SHEET_NAME));
         }
      }

      widgetDef.setHeight(dslWidgetDef.getHeight());
      widgetDef.setDefaultValue(dslWidgetDef.getDefaultValue());
      extractDslWidgetDefOptions(dslWidgetDef.getOption(), SHEET_NAME, widgetDef);
      return widgetDef;
   }

   private void extractDslWidgetDefOptions(EList<String> options, String SHEET_NAME, WidgetDefinition widgetDef) {
      for (String value : options) {
         WidgetOption option = null;
         try {
            option = WidgetOption.valueOf(value);
            widgetDef.getOptions().add(option);
         } catch (IllegalArgumentException ex) {
            OseeLog.log(AtsPlugin.class, Level.WARNING, ex, "Unexpected value [%s] in WorkDefinition [%s] ", value,
               SHEET_NAME);
         }
      }
   }

}
