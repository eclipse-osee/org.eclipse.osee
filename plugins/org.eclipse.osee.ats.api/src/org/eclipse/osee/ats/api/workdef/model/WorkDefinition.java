/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinition extends AbstractWorkDefItem implements IAtsWorkDefinition {

   private final List<IAtsStateDefinition> states = new ArrayList<>(5);
   private String id;
   private IAtsStateDefinition startState;

   public WorkDefinition(String name) {
      super(name);
      this.id = name;
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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (getId() == null ? 0 : getId().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      WorkDefinition other = (WorkDefinition) obj;
      if (getId() == null) {
         if (other.getId() != null) {
            return false;
         } else {
            return false;
         }
      } else if (!getId().equals(other.getId())) {
         return false;
      }
      return true;
   }

   @Override
   public IAtsStateDefinition getStartState() {
      return startState;
   }

   public void setStartState(IAtsStateDefinition startState) {
      this.startState = startState;
   }

   @Override
   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
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
   public String getGuid() {
      return null;
   }

}
