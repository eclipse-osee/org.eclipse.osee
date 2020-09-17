/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.workdef.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinition extends AbstractWorkDefItem implements IAtsWorkDefinition {

   private final List<IAtsStateDefinition> states = new ArrayList<>(5);
   private IAtsStateDefinition startState;
   private HeaderDefinition headerDef;
   private boolean showStateMetrics = false;
   private final List<CreateTasksDefinition> createTasksDefs = new ArrayList<>();
   private final CountingMap<String> labelCount = new CountingMap<String>();

   public WorkDefinition(Long id, String name) {
      this(id, name, null);
   }

   public WorkDefinition(Long id, String name, ArtifactTypeToken artType) {
      super(id, name, artType);
      headerDef = new HeaderDefinition(this);
   }

   @Override
   public IAtsStateDefinition getStateByName(String name) {
      for (IAtsStateDefinition state : states) {
         if (state.getName().equals(name)) {
            return state;
         }
      }
      return null;
   }

   @Override
   public IAtsStateDefinition getStartState() {
      return startState;
   }

   public void setStartState(IAtsStateDefinition startState) {
      this.startState = startState;
   }

   public IAtsStateDefinition addState(IAtsStateDefinition state) {
      IAtsStateDefinition currState = getStateByName(state.getName());
      if (currState != null) {
         throw new IllegalArgumentException("Can not add two states of same name");
      }
      states.add(state);
      return state;
   }

   @Override
   public List<IAtsStateDefinition> getStates() {
      return states;
   }

   @Override
   public HeaderDefinition getHeaderDef() {
      return headerDef;
   }

   @Override
   public boolean hasHeaderDefinitionItems() {
      return headerDef != null && !headerDef.getLayoutItems().isEmpty();
   }

   @Override
   public HeaderDefinition getDefaultHeaderDef() {

      HeaderDefinition defaultHeaderDef = new HeaderDefinition(this);
      defaultHeaderDef.setShowMetricsHeader(true);
      defaultHeaderDef.setShowWorkPackageHeader(true);

      return defaultHeaderDef;
   }

   @Override
   public void setHeaderDefinition(HeaderDefinition headerDef) {
      this.headerDef = headerDef;
   }

   @Override
   public boolean isShowStateMetrics() {
      return showStateMetrics;
   }

   @Override
   public void setShowStateMetrics(boolean showStateMetrics) {
      this.showStateMetrics = showStateMetrics;
   }

   public void addTaskSetDef(CreateTasksDefinition createTasksDef) {
      createTasksDefs.add(createTasksDef);
   }

   @Override
   public List<CreateTasksDefinition> getCreateTasksDefs() {
      return createTasksDefs;
   }

   public CountingMap<String> getLabelCount() {
      return labelCount;
   }

}
