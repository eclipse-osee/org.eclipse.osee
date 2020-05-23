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
