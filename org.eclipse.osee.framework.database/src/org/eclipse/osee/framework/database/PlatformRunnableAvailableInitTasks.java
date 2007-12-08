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
package org.eclipse.osee.framework.database;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

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

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
    */
   public Object start(IApplicationContext context) throws Exception {
      printExtensionPoints("org.eclipse.osee.framework.database.IDbInitializationTask");
      printExtensionPoints("org.eclipse.osee.framework.database.SkynetDbTypes");
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.equinox.app.IApplication#stop()
    */
   public void stop() {
   }
}
