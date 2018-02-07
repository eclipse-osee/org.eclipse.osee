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
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinition extends AbstractWorkDefItem implements IAtsWorkDefinition {

   private final List<IAtsStateDefinition> states = new ArrayList<>(5);
   private IAtsStateDefinition startState;
   private ArtifactToken artifact;

   public WorkDefinition(Long id, String name) {
      super(id, name);
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
   public ArtifactTypeId getArtifactType() {
      return AtsArtifactTypes.WorkDefinition;
   }

   @Override
   public void setStoreObject(ArtifactToken artifact) {
      this.artifact = artifact;
   }

   @Override
   public ArtifactToken getStoreObject() {
      return artifact;
   }

}
