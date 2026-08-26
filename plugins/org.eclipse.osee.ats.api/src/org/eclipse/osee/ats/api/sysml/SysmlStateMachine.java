/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.api.sysml;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a SysML V2 state def (state machine with states and transitions).
 *
 * @author Donald G. Dunne
 */
public class SysmlStateMachine {

   private final String name;
   private final Long workDefinitionId;
   private final List<SysmlState> states = new ArrayList<>();
   private final List<SysmlTransition> transitions = new ArrayList<>();

   public SysmlStateMachine(String name) {
      this(name, null);
   }

   public SysmlStateMachine(String name, Long workDefinitionId) {
      this.name = name;
      this.workDefinitionId = workDefinitionId;
   }

   public String getName() {
      return name;
   }

   public Long getWorkDefinitionId() {
      return workDefinitionId;
   }

   public boolean hasWorkDefinitionId() {
      return workDefinitionId != null;
   }

   public List<SysmlState> getStates() {
      return states;
   }

   public List<SysmlTransition> getTransitions() {
      return transitions;
   }

   public void addState(String stateName, boolean isEntry) {
      states.add(new SysmlState(stateName, isEntry));
   }

   public void addState(String stateName) {
      states.add(new SysmlState(stateName));
   }

   public void addTransition(String source, String target) {
      transitions.add(new SysmlTransition(source, target));
   }

   public void addTransition(String name, String source, String target) {
      transitions.add(new SysmlTransition(name, source, target));
   }
}
