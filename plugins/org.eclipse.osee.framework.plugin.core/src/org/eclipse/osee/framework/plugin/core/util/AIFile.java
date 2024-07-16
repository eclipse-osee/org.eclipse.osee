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

package org.eclipse.osee.framework.plugin.core.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;

/**
 * @author Ryan D. Brooks
 */
public final class AIFile {

   private AIFile() {
      // utility class
   }

   public static void writeToFile(IFile file, InputStream in) {
      try {
         if (file.exists()) {
            file.setCharset("UTF-8", new NullProgressMonitor());
            file.setContents(in, true, false, null); // steam will be closed before return
         } else {
            file.create(in, true, null);
            if (in != null) {
               in.close();
            }
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   public static void writeToFile(IFile file, String string) {
      writeToFile(file, string, "UTF-8");
   }

   public static void writeToFile(IFile file, String string, String charcode) {
      try {
         writeToFile(file, Streams.convertStringToInputStream(string, charcode));
      } catch (UnsupportedEncodingException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   public static IFile constructIFile(String fullPath) {
      return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(fullPath));
   }
}