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
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.osgi.framework.Bundle;

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
