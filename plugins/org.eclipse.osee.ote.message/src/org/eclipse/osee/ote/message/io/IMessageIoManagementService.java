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
package org.eclipse.osee.ote.message.io;

/**
 * @author Ken J. Aguilar
 *
 */
public interface IMessageIoManagementService {

   void install(IMessageIoDriver ioDriver);
   void uninstall(IMessageIoDriver ioDriver);
   void startIO();
   void stopIO();
}
