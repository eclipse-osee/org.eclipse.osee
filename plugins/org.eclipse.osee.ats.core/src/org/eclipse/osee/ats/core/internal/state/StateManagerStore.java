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

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.notify.IAtsNotificationService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G Dunne
 */
public class StateManagerStore {

   public static void writeToStore(IAtsWorkItem workItem, StateManager stateMgr, IAttributeResolver attrResolver, IAtsChangeSet changes, IAtsWorkStateFactory workStateFactory, IAtsNotificationService notifyService) throws OseeCoreException {
      StateManagerWriter writer = new StateManagerWriter(workItem, stateMgr, attrResolver, changes, workStateFactory);
      List<IAtsUser> assigneesAdded = stateMgr.getAssigneesAdded();
      writer.writeToStore();
      changes.addExecuteListener(getPostPersistExecutionListener(workItem, stateMgr, assigneesAdded, attrResolver,
         workStateFactory, notifyService));
   }

   protected static void postPersistNotifyReset(IAtsWorkItem workItem, IAtsStateManager stateMgr, List<IAtsUser> assigneesAdded, IAttributeResolver attrResolver, IAtsWorkStateFactory workStateFactory, IAtsNotificationService notifyService) throws OseeCoreException {
      notifyService.notify(workItem, assigneesAdded, AtsNotifyType.Assigned);
      load(workItem, stateMgr, attrResolver, workStateFactory);
   }

   protected static IExecuteListener getPostPersistExecutionListener(final IAtsWorkItem workItem, final IAtsStateManager stateMgr, final List<IAtsUser> assigneesAdded, final IAttributeResolver attrResolver, final IAtsWorkStateFactory workStateFactory, final IAtsNotificationService notifyService) {
      return new IExecuteListener() {

         @Override
         public void changesStored(IAtsChangeSet changes) {
            try {
               postPersistNotifyReset(workItem, stateMgr, assigneesAdded, attrResolver, workStateFactory, notifyService);
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsCore.class, Level.SEVERE, ex);
            }
         }
      };
   }

   public static void load(IAtsWorkItem workItem, IAtsStateManager stateMgr, IAttributeResolver attrResolver, IAtsWorkStateFactory workStateFactory) throws OseeCoreException {
      StateManager stateManager = (StateManager) stateMgr;
      stateManager.clear();
      StateManagerReader reader = new StateManagerReader(workItem, stateManager, attrResolver, workStateFactory);
      reader.load();
   }

}
