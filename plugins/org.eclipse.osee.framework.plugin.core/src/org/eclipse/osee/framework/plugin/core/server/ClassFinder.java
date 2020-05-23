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

package org.eclipse.osee.framework.plugin.core.server;

import java.io.IOException;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.osgi.framework.Bundle;

/**
 * @author Ken J. Aguilar
 */
public class ClassFinder extends ResourceFinder {

   private final ExportClassLoader loader = new ExportClassLoader();

   @Override
   public byte[] find(String path) throws IOException {
      Bundle bundle = loader.getExportingBundle(path);
      if (bundle != null) {
         return getBytes(bundle.getResource(path).openStream());
      }
      return null;
   }

}
