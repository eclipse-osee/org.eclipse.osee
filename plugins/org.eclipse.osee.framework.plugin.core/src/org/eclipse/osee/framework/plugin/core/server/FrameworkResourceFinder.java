/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Andrew M. Finkbeiner
 */
public class FrameworkResourceFinder extends ResourceFinder {

   @Override
   public byte[] find(String path) throws IOException {
      for (Bundle bundle : FrameworkUtil.getBundle(getClass()).getBundleContext().getBundles()) {
         URL url = bundle.getResource(path);
         if (url != null) {
            return getBytes(url.openStream());
         }
      }
      return null;
   }

}
