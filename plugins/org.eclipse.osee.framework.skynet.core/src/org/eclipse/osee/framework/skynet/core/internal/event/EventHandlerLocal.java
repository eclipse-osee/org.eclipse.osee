/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.skynet.core.internal.event;

import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.FrameworkEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * @author Roberto E. Escobar
 */
public interface EventHandlerLocal<L extends IEventListener, E extends FrameworkEvent> {

   void handle(L listener, Sender sender, E event);

   void send(Transport transport, Sender sender, E event);

}
