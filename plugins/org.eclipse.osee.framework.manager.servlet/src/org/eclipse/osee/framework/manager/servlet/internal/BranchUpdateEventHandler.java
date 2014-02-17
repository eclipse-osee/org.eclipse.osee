/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.event.Event;
import org.eclipse.osee.event.EventHandler;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsConstants;

/**
 * @author Roberto E. Escobar
 */
public class BranchUpdateEventHandler implements EventHandler {

   private Log logger;
   private IDataTranslationService translationService;
   private IApplicationServerLookup lookupService;
   private IApplicationServerManager manager;
   private ExecutorAdmin executor;

   public void setExecutor(ExecutorAdmin executor) {
      this.executor = executor;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setTranslationService(IDataTranslationService translationService) {
      this.translationService = translationService;
   }

   public void setServerLookup(IApplicationServerLookup lookupService) {
      this.lookupService = lookupService;
   }

   public void setAppServerManager(IApplicationServerManager manager) {
      this.manager = manager;
   }

   private boolean isReady() {
      return logger != null && translationService != null && lookupService != null && manager != null;
   }

   @Override
   public void onEvent(Event event) {
      if (isReady()) {
         Collection<Branch> branches = getEventData(event);
         List<Branch> branchToUpdate = new ArrayList<Branch>();
         for (Branch branch : branches) {
            if (!branch.isDirty()) {
               branchToUpdate.add(branch);
            }
         }
         if (!branchToUpdate.isEmpty()) {
            ServerBranchUpdateNotifier notifier =
               new ServerBranchUpdateNotifier(logger, translationService, manager, lookupService, branchToUpdate,
                  executor);
            try {
               notifier.notifyServers();
            } catch (Exception ex) {
               logger.error(ex, "Error notifying other servers");
            }
         }
      }
   }

   @SuppressWarnings("unchecked")
   private Collection<Branch> getEventData(Event event) {
      Object object = event.getValue(OrcsConstants.ORCS_BRANCH_EVENT_DATA);
      Collection<Branch> branches = null;
      if (object instanceof Collection) {
         branches = (Collection<Branch>) object;
      } else {
         branches = Collections.emptyList();
      }
      return branches;
   }
}
