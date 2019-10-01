/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.skynet.core.event.listener.EventQosType;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.FrameworkEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * @author Roberto E. Escobar
 */
public interface OseeEventService {

   boolean isConnected();

   <E extends FrameworkEvent> void send(Object object, E event);

   <E extends RemoteEvent> void receive(E event);

   <E extends FrameworkEvent> void receive(Sender sender, E event);

   void addListener(EventQosType qos, IEventListener listener);

   void removeListener(EventQosType qos, IEventListener listener);

   /**
    * Kick a commit event to this local client to update artifact model for committed artifacts. This is needed cause
    * commit is made on server, but clients need to be notified of updates to commited branch artifact model.
    */
   void sendCommitEvent(Class class1, ArtifactEvent artifactEvent);
}
