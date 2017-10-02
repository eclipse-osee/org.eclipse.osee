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
package org.eclipse.osee.framework.skynet.core.internal.event;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.FrameworkEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * @author Roberto E. Escobar
 */
public interface EventHandlerLocal<L extends IEventListener, E extends FrameworkEvent> {

   void handle(L listener, Sender sender, E event);

   void send(Transport transport, Sender sender, E event) ;

}
