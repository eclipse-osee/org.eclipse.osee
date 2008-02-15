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

package org.eclipse.osee.framework.svn;

import java.io.File;
import java.net.URI;
import java.util.Properties;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.svn.entry.IRepositoryEntry;
import org.eclipse.osee.framework.svn.entry.NullRepositoryEntry;

public class VersionControl {

   private static VersionControl instance;

   public static VersionControl getInstance() {
      if (instance == null) {
         instance = new VersionControl();
      }
      return instance;
   }

   private VersionControl() {
   }

   public URI getLocalFileMatchingRepositoryUrl(String url, String revision) {
      return SvnAPI.getInstance().getLocalFileMatchingRepositoryUrl(url, revision);
   }

   public IRepositoryEntry getRepositoryEntry(File file) {
      if (SvnAPI.getInstance().isSvn(file)) {
         return SvnAPI.getInstance().getSVNInfo(file);
      } else if (isClearcase(file)) {
         return new ClearCaseInfo(file);
      } else {
         return new NullRepositoryEntry();
      }
   }

   public String getRepositoryType(File file) {
      String toReturn = "unknown";
      if (SvnAPI.getInstance().isSvn(file)) {
         toReturn = SvnAPI.getInstance().getVersionControlSystem();
      } else if (isClearcase(file)) {
         toReturn = "clearcase";
      }
      return toReturn;
   }

   public void checkOut(String[] fileToCheckout, IProgressMonitor monitor) {
      SvnAPI.getInstance().checkOut(fileToCheckout, monitor);
   }

   private boolean isClearcase(File file) {
      Properties p = System.getProperties();
      // Unix
      if (p.getProperty("file.separator").equals("/")) {
         File ctFile = new File("/usr/src/rational/2003.06.00/rhat_x86/clearcase/rhat_x86/bin/cleartool");

         if (ctFile.exists())
            return true;
         else
            return false;
      }
      // Windows
      if (p.getProperty("file.separator").equals("\\")) {
         File ctFile = new File("C:\\Program Files\\Rational\\ClearCase\\bin\\cleartool.exe");

         if (ctFile.exists())
            return true;
         else
            return false;
      }
      return false;
   }
}
