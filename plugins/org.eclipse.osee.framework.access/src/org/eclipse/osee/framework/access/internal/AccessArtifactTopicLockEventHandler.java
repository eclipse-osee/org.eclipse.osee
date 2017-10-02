/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
