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
package org.eclipse.osee.framework.plugin.core.server;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.internal.Activator;
import org.osgi.framework.Bundle;

public class BundleResourceFinder extends ResourceFinder {
   private final ArrayList<Bundle> bundles;

   public BundleResourceFinder(String[] bundlenames) {
      bundles = new ArrayList<>();
      for (int i = 0; i < bundlenames.length; i++) {
         Bundle bundle = Platform.getBundle(bundlenames[i]);
         if (bundle != null) {
            bundles.add(Platform.getBundle(bundlenames[i]));
         } else {
            OseeLog.logf(Activator.class, Level.SEVERE,
               "Unable to load bundle [ %s ].  This bundle was not added to the list in BundleResourceFinder.",
               bundlenames[i]);
         }
      }
   }

   @Override
   public byte[] find(String path) throws IOException {
      for (int i = 0; i < bundles.size(); i++) {
         URL url = bundles.get(i).getResource(path);
         if (url != null) {
            return getBytes(url.openStream());
         }
      }
      return null;
   }

   @Override
   public void dispose() {
      bundles.clear();
   }

}
