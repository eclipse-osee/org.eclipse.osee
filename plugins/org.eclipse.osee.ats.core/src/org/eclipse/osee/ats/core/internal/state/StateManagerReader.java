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
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.core.workflow.state.AtsWorkStateFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G Dunne
 */
public class StateManagerReader {

   private final StateManager stateMgr;
   private final IAttributeResolver attrResolver;
   private final IAtsWorkItem workItem;

   public StateManagerReader(IAtsWorkItem workItem, StateManager stateMgr, IAttributeResolver attrResolver) {
      this.workItem = workItem;
      this.stateMgr = stateMgr;
      this.attrResolver = attrResolver;
   }

   public synchronized void load() throws OseeCoreException {
      String currentStateXml = attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, "");
      if (Strings.isValid(currentStateXml)) {
         WorkStateImpl currentState = AtsWorkStateFactory.getFromXml(currentStateXml);
         stateMgr.setCurrentStateName(currentState.getName());
         stateMgr.addState(currentState);
         for (String stateXml : attrResolver.getAttributesToStringList(workItem, AtsAttributeTypes.State)) {
            WorkStateImpl state = AtsWorkStateFactory.getFromXml(stateXml);
            if (!state.getName().equals(currentState.getName())) {
               if (!state.getName().equals(currentState.getName())) {
                  stateMgr.addState(state);
               }
            }
         }
      }
      Integer percentComplete = attrResolver.getSoleAttributeValue(workItem, AtsAttributeTypes.PercentComplete, null);
      stateMgr.setPercentCompleteValue(percentComplete);
      stateMgr.setDirty(false);
      stateMgr.getInitialAssignees().addAll(stateMgr.getAssignees());
   }

}
