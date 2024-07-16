/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.internal.state;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G Dunne
 */
public class StateManagerStore {

   public static void writeToStore(AtsUser asUser, IAtsWorkItem workItem, StateManager stateMgr, IAttributeResolver attrResolver, IAtsChangeSet changes, IAtsWorkStateFactory workStateFactory) {
      StateManagerWriter writer = new StateManagerWriter(workItem, stateMgr, attrResolver, changes, workStateFactory);
      List<AtsUser> assigneesAdded = stateMgr.getAssigneesAdded();
      writer.writeToStore();
      changes.addExecuteListener(getPostPersistExecutionListener(asUser, workItem, stateMgr, assigneesAdded,
         attrResolver, workStateFactory, changes));
   }

   protected static void postPersistNotifyReset(AtsUser asUser, IAtsWorkItem workItem, IAtsStateManager stateMgr, List<AtsUser> assigneesAdded, IAttributeResolver attrResolver, IAtsWorkStateFactory workStateFactory, IAtsChangeSet changes) {
      try {
         if (!assigneesAdded.isEmpty()) {
            changes.addWorkItemNotificationEvent(AtsNotificationEventFactory.getWorkItemNotificationEvent(asUser,
               workItem, assigneesAdded, AtsNotifyType.Assigned));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(StateManagerStore.class, Level.SEVERE, "Error adding ATS Notification Event", ex);
      }
      load(workItem, stateMgr, attrResolver, workStateFactory);
   }

   protected static IExecuteListener getPostPersistExecutionListener(final AtsUser asUser, final IAtsWorkItem workItem, final IAtsStateManager stateMgr, final List<AtsUser> assigneesAdded, final IAttributeResolver attrResolver, final IAtsWorkStateFactory workStateFactory, final IAtsChangeSet changes) {
      return new IExecuteListener() {

         @Override
         public void changesStored(IAtsChangeSet changes) {
            try {
               postPersistNotifyReset(asUser, workItem, stateMgr, assigneesAdded, attrResolver, workStateFactory,
                  changes);
            } catch (OseeCoreException ex) {
               OseeLog.log(StateManagerStore.class, Level.SEVERE, ex);
            }
         }
      };
   }

   public static void load(IAtsWorkItem workItem, IAtsStateManager stateMgr, IAttributeResolver attrResolver, IAtsWorkStateFactory workStateFactory) {
      try {
         StateManager stateManager = (StateManager) stateMgr;
         stateManager.clear();
         StateManagerReader reader = new StateManagerReader(workItem, stateManager, attrResolver, workStateFactory);
         reader.load();
      } catch (Exception ex) {
         OseeLog.log(StateManagerStore.class, Level.SEVERE,
            String.format("Exception loading state manager work work item %s", workItem.toStringWithId()), ex);
         throw ex;
      }
   }

}
