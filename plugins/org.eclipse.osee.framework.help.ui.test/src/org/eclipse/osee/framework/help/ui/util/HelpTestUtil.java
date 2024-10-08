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

package org.eclipse.osee.framework.help.ui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Angel Avila
 */
public final class HelpTestUtil {

   private HelpTestUtil() {
      // Utility Class
   }

   public static URL getResource(String resource) throws Exception {
      Bundle bundle = FrameworkUtil.getBundle(HelpTestUtil.class);
      URL url = bundle.getResource(resource);

      assertNotNull(String.format("Resource not found: [%s]", resource), url);

      url = FileLocator.toFileURL(url);
      File file = new File(url.toURI());
      assertEquals(String.format("[%s] does not exist", resource), true, file.exists());
      assertEquals(String.format("[%s] unreadable", resource), true, file.canRead());

      return url;
   }
}
