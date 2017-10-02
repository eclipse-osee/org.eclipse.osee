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
package org.eclipse.osee.ats.core.internal.state;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G Dunne
 */
public class StateManagerReader {

   private final StateManager stateMgr;
   private final IAttributeResolver attrResolver;
   private final IAtsWorkItem workItem;
   private final IAtsWorkStateFactory workStateFactory;

   public StateManagerReader(IAtsWorkItem workItem, StateManager stateMgr, IAttributeResolver attrResolver, IAtsWorkStateFactory workStateFactory) {
      this.workItem = workItem;
      this.stateMgr = stateMgr;
      this.attrResolver = attrResolver;
      this.workStateFactory = workStateFactory;
   }

   public synchronized void load() {
      String currentStateXml = attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, "");
      if (Strings.isValid(currentStateXml)) {
         WorkState currentState = workStateFactory.fromStoreStr(currentStateXml);
         stateMgr.setCurrentStateName(currentState.getName());
         stateMgr.addState(currentState);
         for (String stateXml : attrResolver.getAttributesToStringList(workItem, AtsAttributeTypes.State)) {
            WorkState state = workStateFactory.fromStoreStr(stateXml);
            if (!state.getName().equals(currentState.getName())) {
               if (!state.getName().equals(currentState.getName())) {
                  stateMgr.addState(state);
               }
            }
         }
      }
      Integer percentComplete = attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.PercentComplete, null);
      stateMgr.setStateType(
         StateType.valueOf(attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentStateType, null)));
      stateMgr.setPercentCompleteValue(percentComplete);
      stateMgr.setDirty(false);
      stateMgr.getInitialAssignees().addAll(stateMgr.getAssignees());
   }

}
