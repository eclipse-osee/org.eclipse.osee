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
package org.eclipse.osee.framework.core.server.internal;

/**
 * @author Roberto E. Escobar
 */
class OseeServerThread extends Thread {

   protected OseeServerThread(String name) {
      super(name);

   }

   protected OseeServerThread(Runnable arg0, String name) {
      super(arg0, name);
   }
}
