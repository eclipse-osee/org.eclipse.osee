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

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.core.workflow.state.AtsWorkStateFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G Dunne
 */
public class StateManagerWriter {

   private final IAttributeResolver attrResolver;
   private final IAtsWorkItem workItem;
   private final StateManager stateMgr;
   private final IAtsChangeSet changes;

   public StateManagerWriter(IAtsWorkItem workItem, StateManager stateMgr, IAttributeResolver attrResolver, IAtsChangeSet changes) {
      this.workItem = workItem;
      this.stateMgr = stateMgr;
      this.attrResolver = attrResolver;
      this.changes = changes;
   }

   public void writeToStore() throws OseeCoreException {

      String currentStateName = stateMgr.getCurrentStateName();
      attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState,
         AtsWorkStateFactory.toXml(stateMgr, currentStateName), changes);

      removeCurrentStateAttributeIfExists(currentStateName, changes);

      writeStatesToStore(changes);

      StateType stateType = workItem.getWorkDefinition().getStateByName(currentStateName).getStateType();
      attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.CurrentStateType, stateType.name(), changes);
      Integer percentCompleteValue = stateMgr.getPercentCompleteValue();
      if (percentCompleteValue == null) {
         percentCompleteValue = 0;
      }
      attrResolver.setSoleAttributeValue(workItem, AtsAttributeTypes.PercentComplete, percentCompleteValue, changes);
   }

   private void writeStatesToStore(IAtsChangeSet changes) throws OseeCoreException {
      for (String stateName : stateMgr.getVisitedStateNames()) {
         if (!stateName.equals(stateMgr.getCurrentStateName())) {
            boolean updated = updateStateAttributeIfExsists(stateName, changes);
            // Else, doesn't exist yet, create
            if (!updated) {
               attrResolver.addAttribute(workItem, AtsAttributeTypes.State,
                  AtsWorkStateFactory.toXml(workItem.getStateMgr(), stateName), changes);
            }
         }
      }
   }

   private void removeCurrentStateAttributeIfExists(String stateName, IAtsChangeSet changes) throws OseeCoreException {
      Collection<IAttribute<String>> attrs = attrResolver.getAttributes(workItem, AtsAttributeTypes.State);
      for (IAttribute<String> attr : attrs) {
         WorkStateImpl storedState = AtsWorkStateFactory.getFromXml(attr.getValue());
         if (stateName.equals(storedState.getName())) {
            attrResolver.deleteAttribute(workItem, attr, changes);
         }
      }
   }

   private boolean updateStateAttributeIfExsists(String stateName, IAtsChangeSet changes) throws OseeCoreException {
      // Update attribute if it already exists
      Collection<IAttribute<String>> attrs = attrResolver.getAttributes(workItem, AtsAttributeTypes.State);
      for (IAttribute<String> attr : attrs) {
         WorkStateImpl storedState = AtsWorkStateFactory.getFromXml(attr.getValue());
         if (stateName.equals(storedState.getName())) {
            attrResolver.setValue(workItem, attr, AtsAttributeTypes.State,
               AtsWorkStateFactory.toXml(workItem.getStateMgr(), stateName), changes);
            return true;
         }
      }
      return false;
   }

}
