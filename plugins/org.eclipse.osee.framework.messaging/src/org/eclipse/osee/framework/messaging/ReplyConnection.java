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
package org.eclipse.osee.framework.messaging;

/**
 * @author Andrew M. Finkbeiner
 */
public interface ReplyConnection {
   public boolean isReplyRequested();

   public void send(Object body, Class<?> clazz, final OseeMessagingStatusCallback statusCallback);
}
