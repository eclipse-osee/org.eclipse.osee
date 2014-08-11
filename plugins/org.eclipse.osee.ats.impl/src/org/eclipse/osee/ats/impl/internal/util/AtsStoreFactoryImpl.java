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
package org.eclipse.osee.ats.impl.internal.util;

import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreFactory;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsStoreFactoryImpl implements IAtsStoreFactory {

   private final OrcsApi orcsApi;
   private final IAtsStateFactory stateFactory;
   private final IAtsLogFactory logFactory;
   private final IAtsNotifier notifier;

   public AtsStoreFactoryImpl(OrcsApi orcsApi, IAtsStateFactory stateFactory, IAtsLogFactory logFactory, IAtsNotifier notifier) {
      this.logFactory = logFactory;
      this.stateFactory = stateFactory;
      this.orcsApi = orcsApi;
      this.notifier = notifier;
   }

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment, IAtsUser user) {
      return new AtsChangeSet(orcsApi, stateFactory, logFactory, comment, user, notifier);
   }

}
