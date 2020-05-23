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

package org.eclipse.osee.framework.database.init.internal;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * @author Roberto E. Escobar
 */
public class PlatformRunnableAvailableInitTasks implements IApplication {

   private void printExtensionPoints(String pointId) {
      System.out.println("Point [" + pointId + "] search results:");
      IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(pointId);
      if (point != null) {
         for (IExtension ex : point.getExtensions()) {
            System.out.println("   " + ex.getNamespaceIdentifier() + "/" + ex.getUniqueIdentifier());
         }
      }
   }

   @Override
   public Object start(IApplicationContext context) throws Exception {
      printExtensionPoints("org.eclipse.osee.framework.database.IDbInitializationTask");
      printExtensionPoints("org.eclipse.osee.framework.database.SkynetDbTypes");
      return null;
   }

   @Override
   public void stop() {
      //
   }
}
