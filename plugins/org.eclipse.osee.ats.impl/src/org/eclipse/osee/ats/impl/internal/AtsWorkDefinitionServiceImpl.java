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
package org.eclipse.osee.ats.impl.internal;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionStore;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.dsl.ModelUtil;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.impl.internal.convert.ConvertAtsDslToWorkDefinition;
import org.eclipse.osee.ats.impl.internal.convert.ConvertWorkDefinitionToAtsDsl;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Provides new and stored Work Definitions
 * 
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionServiceImpl implements IAtsWorkDefinitionService {

   private Map<String, IAtsWorkDefinition> workDefIdToWorkDef;

   private IAtsWorkDefinitionStore workDefStore;
   private IAttributeResolver attrResolver;
   private IAtsUserService userService;

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
      Conditions.checkNotNull(workDefStore, "IAtsWorkDefinitionStore");
      Conditions.checkNotNull(attrResolver, "IAttributeResolver");
      Conditions.checkNotNull(userService, "IAtsWorkDefinitionStore");
   }

   public void stop() {
      workDefIdToWorkDef = null;
   }

   @Override
   public IAtsWorkDefinition copyWorkDefinition(String newName, IAtsWorkDefinition workDef, XResultData resultData) {
      ConvertWorkDefinitionToAtsDsl converter = new ConvertWorkDefinitionToAtsDsl(resultData);
      AtsDsl atsDsl = converter.convert(newName, workDef);

      // Convert back to WorkDefinition
      ConvertAtsDslToWorkDefinition converter2 =
         new ConvertAtsDslToWorkDefinition(newName, atsDsl, resultData, attrResolver, userService);
      IAtsWorkDefinition newWorkDef = converter2.convert();
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

   private static class StringOutputStream extends OutputStream {
      private final StringBuilder string = new StringBuilder();

      @Override
      public void write(int b) {
         this.string.append((char) b);
      }

      @Override
      public String toString() {
         return this.string.toString();
      }
   };

   private void ensureLoaded(XResultData resultData) throws Exception {
      if (workDefIdToWorkDef == null) {
         workDefIdToWorkDef = new HashMap<String, IAtsWorkDefinition>(15);
         for (Pair<String, String> entry : workDefStore.getWorkDefinitionStrings()) {
            String name = entry.getFirst();
            String workDefStr = entry.getSecond();
            AtsDsl atsDsl = ModelUtil.loadModel(name + ".ats", workDefStr);
            ConvertAtsDslToWorkDefinition convert =
               new ConvertAtsDslToWorkDefinition(name, atsDsl, resultData, attrResolver, userService);
            IAtsWorkDefinition workDef = convert.convert();
            if (workDefIdToWorkDef != null) {
               workDefIdToWorkDef.put(name, workDef);
            }
         }
      }
   }

   @Override
   public IAtsWorkDefinition getWorkDef(String workDefId, XResultData resultData) throws Exception {
      ensureLoaded(resultData);
      String workDefStr = workDefStore.loadWorkDefinitionString(workDefId);
      AtsDsl atsDsl = ModelUtil.loadModel(workDefId + ".ats", workDefStr);
      ConvertAtsDslToWorkDefinition convert =
         new ConvertAtsDslToWorkDefinition(workDefId, atsDsl, resultData, attrResolver, userService);
      return convert.convert();
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
      List<String> names = new ArrayList<String>();
      for (IAtsStateDefinition state : workDef.getStates()) {
         names.add(state.getName());
      }
      return names;
   }

   @Override
   public List<IAtsStateDefinition> getStatesOrderedByOrdinal(IAtsWorkDefinition workDef) {
      List<IAtsStateDefinition> orderedPages = new ArrayList<IAtsStateDefinition>();
      List<IAtsStateDefinition> unOrderedPages = new ArrayList<IAtsStateDefinition>();
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
      List<IAtsWidgetDefinition> widgets = new ArrayList<IAtsWidgetDefinition>();
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
      ConvertAtsDslToWorkDefinition convert =
         new ConvertAtsDslToWorkDefinition(Strings.unquote(atsDsl.getWorkDef().getName()), atsDsl, result,
            attrResolver, userService);
      if (!result.isEmpty()) {
         throw new IllegalStateException(result.toString());
      }
      return convert.convert();
   }

   @Override
   public void clearCaches() {
      workDefIdToWorkDef = null;
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
}
