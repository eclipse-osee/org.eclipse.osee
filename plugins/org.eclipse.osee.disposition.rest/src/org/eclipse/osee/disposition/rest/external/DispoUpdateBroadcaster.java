/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.external;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.logger.Log;

/**
 * @author Angel Avila
 * @author Megumi Telles
 */
public class DispoUpdateBroadcaster {

   private Log logger;
   private final Set<DispoListenerApi> listeners = new HashSet<>();

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start() {
      logger.trace("Starting DispoUpdateBroadcaster...");
   }

   public void stop() {
      logger.trace("Stopping DispoUpdateBroadcaster...");
   }

   public void addDispoListener(DispoListenerApi listener) {
      listeners.add(listener);
   }

   public void broadcastUpdateItems(Collection<String> ids, Collection<DispoItem> items, DispoSet set) {
      for (DispoListenerApi listener : listeners) {
         listener.onUpdateItemStats(ids, items, set);
      }
   }
}
