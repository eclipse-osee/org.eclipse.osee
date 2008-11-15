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

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osee.framework.database.initialize.DatabaseInitializationOperation;

public class ConfigDatabase implements IApplication {

   private static String[] bundlesToStart =
         new String[] {"org.eclipse.osee.framework.db.connection", "org.eclipse.equinox.ds"};

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
    */
   public Object start(IApplicationContext context) throws Exception {
      for (String symbolicName : bundlesToStart) {
         Platform.getBundle(symbolicName).start();
      }
      DatabaseInitializationOperation.executeConfigureFromJvmProperties();
      return EXIT_OK;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.equinox.app.IApplication#stop()
    */
   public void stop() {
   }

}
