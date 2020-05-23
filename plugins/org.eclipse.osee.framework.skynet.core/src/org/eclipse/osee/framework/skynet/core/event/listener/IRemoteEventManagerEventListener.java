/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.event.listener;

import org.eclipse.osee.framework.skynet.core.event.model.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * @author Donald G. Dunne
 */
public interface IRemoteEventManagerEventListener extends IEventListener {
   public void handleRemoteEventManagerEvent(Sender sender, RemoteEventServiceEventType remoteEventServiceEventType);

}
