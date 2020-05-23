/*********************************************************************
 * Copyright (c) 2013 Boeing
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
