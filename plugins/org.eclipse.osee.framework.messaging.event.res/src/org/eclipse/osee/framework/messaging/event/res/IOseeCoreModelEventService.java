/*********************************************************************
 * Copyright (c) 2010 Boeing
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
