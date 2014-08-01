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
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsStateFactory implements IAtsStateFactory {

   private final IAttributeResolver attrResolver;
   private final IAtsWorkStateFactory workStateFactory;

   public AtsStateFactory(IAttributeResolver attrResolver, IAtsWorkStateFactory workStateFactory) {
      this.attrResolver = attrResolver;
      this.workStateFactory = workStateFactory;
   }

   @Override
   public IAtsStateManager getStateManager(IAtsWorkItem workItem) {
      StateManager stateMgr = new StateManager(workItem);
      return stateMgr;
   }

   @Override
   public IAtsStateManager getStateManager(IAtsWorkItem workItem, boolean load) throws OseeCoreException {
      IAtsStateManager stateMgr = getStateManager(workItem);
      if (load) {
         StateManagerStore.load(workItem, stateMgr, attrResolver, workStateFactory);
      }
      return stateMgr;
   }

   @Override
   public void writeToStore(IAtsUser asUser, IAtsWorkItem workItem, IAtsChangeSet changes) throws OseeCoreException {
      StateManagerStore.writeToStore(asUser, workItem, (StateManager) workItem.getStateMgr(), attrResolver, changes,
         workStateFactory);
   }

   @Override
   public void load(IAtsWorkItem workItem, IAtsStateManager stateMgr) {
      StateManagerStore.load(workItem, stateMgr, attrResolver, workStateFactory);
   }

}
