/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.dsl.integration.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.IWorkDefinitionStringProvider;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionStore;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.dsl.ModelUtil;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.StringOutputStream;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.logger.Log;

/**
 * Provides new and stored Work Definitions
 *
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionServiceImpl implements IAtsWorkDefinitionService {

   private IAtsWorkDefinitionStore workDefStore;
   private IAttributeResolver attrResolver;
   private IAtsUserService userService;
   private Log logger;
   private IWorkDefinitionStringProvider workDefinitionStringProvider;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setAtsWorkDefinitionStore(IAtsWorkDefinitionStore workDefStore) {
      this.workDefStore = workDefStore;
   }

   public void setAttributeResolver(IAttributeResolver attrResolver) {
      this.attrResolver = attrResolver;
   }

   public void setAtsUserService(IAtsUserService userService) {
      this.userService = userService;
   }

   public void start() throws OseeCoreException {
      logger.info("AtsWorkDefinitionServiceImpl started");
   }

   @Override
   public IAtsWorkDefinition copyWorkDefinition(String newName, IAtsWorkDefinition workDef, XResultData resultData) {
      ConvertWorkDefinitionToAtsDsl converter = new ConvertWorkDefinitionToAtsDsl(resultData);
      AtsDsl atsDsl = converter.convert(newName, workDef);

      // Convert back to WorkDefinition
      ConvertAtsDslToWorkDefinition converter2 =
         new ConvertAtsDslToWorkDefinition(newName, atsDsl, resultData, attrResolver, userService);
      IAtsWorkDefinition newWorkDef = converter2.convert().iterator().next();
      return newWorkDef;
   }

   @Override
   public String getStorageString(IAtsWorkDefinition workDef, XResultData resultData) throws Exception {
      ConvertWorkDefinitionToAtsDsl converter = new ConvertWorkDefinitionToAtsDsl(resultData);
      AtsDsl atsDsl = converter.convert(workDef.getName(), workDef);
      StringOutputStream writer = new StringOutputStream();
      ModelUtil.saveModel(atsDsl, "ats:/mock" + Lib.getDateTimeString() + ".ats", writer);
      return writer.toString();
   }

   @Override
   public List<IAtsRuleDefinition> getRuleDefinitions() {
      List<IAtsRuleDefinition> ruleDefs = new ArrayList<>();
      String ruleDefintionsDslStr = workDefStore.loadRuleDefinitionString();
      if (Strings.isValid(ruleDefintionsDslStr)) {
         AtsDsl atsDsl;
         try {
            atsDsl = ModelUtil.loadModel("Rule Definitions" + ".ats", ruleDefintionsDslStr);
            ConvertAtsDslToRuleDefinition convert = new ConvertAtsDslToRuleDefinition(atsDsl, ruleDefs, userService);
            ruleDefs = convert.convert();
         } catch (Exception ex) {
            OseeLog.log(AtsWorkDefinitionServiceImpl.class, Level.SEVERE, ex);
         }
      }
      return ruleDefs;
   }

   @Override
   public IAtsWorkDefinition getWorkDef(String workDefId, XResultData resultData) throws Exception {
      Conditions.checkNotNullOrEmpty(workDefId, "workDefId");
      String workDefStr = null;
      if (workDefinitionStringProvider != null && workDefinitionStringProvider.getWorkDefIdToWorkDef() != null) {
         workDefStr = workDefinitionStringProvider.getWorkDefIdToWorkDef().get(workDefId);
      }
      if (workDefStr == null) {
         workDefStr = workDefStore.loadWorkDefinitionString(workDefId);
      }
      Conditions.checkNotNullOrEmpty(workDefStr, "workDefStr");
      AtsDsl atsDsl = ModelUtil.loadModel(workDefId + ".ats", workDefStr);
      ConvertAtsDslToWorkDefinition convert =
         new ConvertAtsDslToWorkDefinition(workDefId, atsDsl, resultData, attrResolver, userService);
      for (IAtsWorkDefinition workDef : convert.convert()) {
         if (workDef.getId().equals(workDefId)) {
            return workDef;
         }
      }
      return null;
   }

   @Override
   public boolean isStateWeightingEnabled(IAtsWorkDefinition workDef) {
      for (IAtsStateDefinition stateDef : workDef.getStates()) {
         if (stateDef.getStateWeight() != 0) {
            return true;
         }
      }
      return false;
   }

   @Override
   public Collection<String> getStateNames(IAtsWorkDefinition workDef) {
      List<String> names = new ArrayList<>();
      for (IAtsStateDefinition state : workDef.getStates()) {
         names.add(state.getName());
      }
      return names;
   }

   @Override
   public List<IAtsStateDefinition> getStatesOrderedByOrdinal(IAtsWorkDefinition workDef) {
      List<IAtsStateDefinition> orderedPages = new ArrayList<>();
      List<IAtsStateDefinition> unOrderedPages = new ArrayList<>();
      for (int x = 1; x < workDef.getStates().size() + 1; x++) {
         for (IAtsStateDefinition state : workDef.getStates()) {
            if (state.getOrdinal() == x) {
               orderedPages.add(state);
            } else if (state.getOrdinal() == 0 && !unOrderedPages.contains(state)) {
               unOrderedPages.add(state);
            }
         }
      }
      orderedPages.addAll(unOrderedPages);
      return orderedPages;
   }

   @Override
   public void getStatesOrderedByDefaultToState(IAtsWorkDefinition workDef, IAtsStateDefinition stateDefinition, List<IAtsStateDefinition> pages) {
      if (pages.contains(stateDefinition)) {
         return;
      }
      // Add this page first
      pages.add(stateDefinition);
      // Add default page
      IAtsStateDefinition defaultToState = stateDefinition.getDefaultToState();
      if (defaultToState != null && !defaultToState.getName().equals(stateDefinition.getName())) {
         getStatesOrderedByDefaultToState(workDef, stateDefinition.getDefaultToState(), pages);
      }
      // Add remaining pages
      for (IAtsStateDefinition stateDef : stateDefinition.getToStates()) {
         if (!pages.contains(stateDef)) {
            getStatesOrderedByDefaultToState(workDef, stateDef, pages);
         }
      }
   }

   /**
    * Recursively decend StateItems and grab all widgetDefs.<br>
    * <br>
    * Note: Modifing this list will not affect the state widgets. Use addStateItem().
    */
   @Override
   public List<IAtsWidgetDefinition> getWidgetsFromLayoutItems(IAtsStateDefinition stateDef) {
      List<IAtsWidgetDefinition> widgets = new ArrayList<>();
      getWidgets(stateDef, widgets, stateDef.getLayoutItems());
      return widgets;
   }

   private static void getWidgets(IAtsStateDefinition stateDef, List<IAtsWidgetDefinition> widgets, List<IAtsLayoutItem> stateItems) {
      for (IAtsLayoutItem stateItem : stateItems) {
         if (stateItem instanceof IAtsCompositeLayoutItem) {
            getWidgets(stateDef, widgets, ((IAtsCompositeLayoutItem) stateItem).getaLayoutItems());
         } else if (stateItem instanceof IAtsWidgetDefinition) {
            widgets.add((IAtsWidgetDefinition) stateItem);
         }
      }
   }

   @Override
   public boolean hasWidgetNamed(IAtsStateDefinition stateDef, String name) {
      for (IAtsWidgetDefinition widgetDef : getWidgetsFromLayoutItems(stateDef)) {
         if (widgetDef.getName().equals(name)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition(String workDefinitionDsl) throws Exception {
      AtsDsl atsDsl = ModelUtil.loadModel("model.ats", workDefinitionDsl);
      XResultData result = new XResultData(false);
      ConvertAtsDslToWorkDefinition convert = new ConvertAtsDslToWorkDefinition(
         Strings.unquote(atsDsl.getWorkDef().iterator().next().getName()), atsDsl, result, attrResolver, userService);
      if (!result.isEmpty()) {
         throw new IllegalStateException(result.toString());
      }
      return convert.convert().iterator().next();
   }

   @Override
   public boolean teamDefHasRule(IAtsWorkItem workItem, RuleDefinitionOption option) {
      boolean hasRule = false;
      IAtsTeamWorkflow teamWf = null;
      try {
         if (workItem instanceof IAtsTeamWorkflow) {
            teamWf = (IAtsTeamWorkflow) workItem;
         } else if (this instanceof IAtsAbstractReview) {
            teamWf = ((IAtsAbstractReview) this).getParentTeamWorkflow();
         }
         if (teamWf != null) {
            hasRule = teamWf.getTeamDefinition().hasRule(option.name());
         }
      } catch (Exception ex) {
         OseeLog.log(AtsWorkDefinitionServiceImpl.class, Level.SEVERE, ex);
      }
      return hasRule;
   }

   @Override
   public boolean isInState(IAtsWorkItem workItem, IAtsStateDefinition stateDef) {
      return workItem.getStateMgr().getCurrentStateName().equals(stateDef.getName());
   }

   @Override
   public Collection<IAtsWorkDefinition> getAllWorkDefinitions(XResultData resultData) {
      List<IAtsWorkDefinition> workDefs = new ArrayList<>();
      if (workDefinitionStringProvider != null && workDefinitionStringProvider.getWorkDefIdToWorkDef() != null) {
         for (Entry<String, String> entry : workDefinitionStringProvider.getWorkDefIdToWorkDef().entrySet()) {
            String name = entry.getKey();
            String workDefStr = entry.getValue();
            processWorkDef(resultData, workDefs, name, workDefStr);
         }
      } else {
         for (Pair<String, String> entry : workDefStore.getWorkDefinitionStrings()) {
            String name = entry.getFirst();
            String workDefStr = entry.getSecond();
            processWorkDef(resultData, workDefs, name, workDefStr);
         }
      }
      return workDefs;
   }

   private void processWorkDef(XResultData resultData, List<IAtsWorkDefinition> workDefs, String name, String workDefStr) {
      try {
         AtsDsl atsDsl = ModelUtil.loadModel(name + ".ats", workDefStr);
         ConvertAtsDslToWorkDefinition convert =
            new ConvertAtsDslToWorkDefinition(name, atsDsl, resultData, attrResolver, userService);
         for (IAtsWorkDefinition workDef : convert.convert()) {
            workDefs.add(workDef);
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   @Override
   public Collection<String> getAllValidStateNames(XResultData resultData) {
      Set<String> allValidStateNames = new HashSet<>();
      for (IAtsWorkDefinition workDef : getAllWorkDefinitions(resultData)) {
         for (String stateName : getStateNames(workDef)) {
            if (!allValidStateNames.contains(stateName)) {
               allValidStateNames.add(stateName);
            }
         }
      }
      return allValidStateNames;
   }

   @Override
   public void setWorkDefinitionStringProvider(IWorkDefinitionStringProvider workDefinitionStringProvider) {
      this.workDefinitionStringProvider = workDefinitionStringProvider;
   }

}
