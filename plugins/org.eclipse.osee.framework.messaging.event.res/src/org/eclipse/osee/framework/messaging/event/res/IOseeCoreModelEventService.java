/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.event.res;

import org.eclipse.osee.framework.messaging.ConnectionListener;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeCoreModelEventService {

   void addFrameworkListener(IFrameworkEventListener frameworkEventListener);

   void removeFrameworkListener(IFrameworkEventListener frameworkEventListener);

   void addConnectionListener(ConnectionListener connectionListener);

   void removeConnectionListener(ConnectionListener connectionListener);

   void sendRemoteEvent(RemoteEvent remoteEvent);

}
