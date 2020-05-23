/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.access.internal;

import java.util.logging.Level;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.model.AccessTopicEvent;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Handler for {@link AccessTopicEvent.ACCESS_ARTIFACT_LOCK_MODIFIED}
 *
 * @author Donald G. Dunne
 */
public class AccessArtifactTopicLockEventHandler implements EventHandler {

   @Override
   public void handleEvent(Event event) {
      try {
         AccessControlManager.clearCaches();
      } catch (Exception ex) {
         OseeLog.log(AccessControlManager.class, Level.SEVERE, ex);
      }
   }

}
