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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osee.framework.skynet.core.linking.HttpServer;

/**
 * @author Roberto E. Escobar
 */
public class SkynetHttpServerApplication implements IApplication {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
    */
   public Object start(IApplicationContext context) throws Exception {
      HttpServer.remoteServerStartup();
      while (true)
         ;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.equinox.app.IApplication#stop()
    */
   public void stop() {
   }

}
