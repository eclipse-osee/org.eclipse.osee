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

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.messaging.ConnectionListener;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeCoreModelEventService {

   void addFrameworkListener(IFrameworkEventListener frameworkEventListener) throws OseeCoreException;

   void removeFrameworkListener(IFrameworkEventListener frameworkEventListener) throws OseeCoreException;

   void addConnectionListener(ConnectionListener connectionListener) throws OseeCoreException;

   void removeConnectionListener(ConnectionListener connectionListener) throws OseeCoreException;

   void sendRemoteEvent(RemoteEvent remoteEvent) throws OseeCoreException;

}
