/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.future;


/**
 * @author b1528444
 *
 */
public interface OseeMessaging {
   
   public void addListener(Component component, String topic, OseeMessagingListener listener) throws Exception;
   
   public void sendMessage(Component component, String topic, Object body);
   
}
