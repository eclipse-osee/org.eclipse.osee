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
package org.eclipse.osee.framework.updater;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class BaseInstallJob implements IInstallJob {

   public File getFile(String path) throws IOException {

      URL url =
            FileLocator.find(Platform.getBundle("org.eclipse.osee.framework.updater"), new Path("dlResources/" + path),
                  null);
      url = FileLocator.resolve(url);
      return new File(url.getFile());
   }

}
