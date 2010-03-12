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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.eclipse.osee.framework.svn.entry.IRepositoryEntry;

public class ClearCaseInfo implements IRepositoryEntry {

   private File file;

   public ClearCaseInfo(File tmpFile) {
      super();
      file = tmpFile;
   }

   public String getURL() {
      return file.getAbsolutePath();
   }

   public String getVersion() {
      try {
         Process ct = Runtime.getRuntime().exec("cleartool lshistory -last " + file.getAbsolutePath());
         BufferedReader reader = new BufferedReader(new InputStreamReader(ct.getInputStream()));
         String lineRead = null;
         if ((lineRead = reader.readLine()) != null) {
            return lineRead;
         } else
            return "unknown";
      } catch (IOException ex) {
         System.out.println("ClearCase not found");
      }

      return "unknown";
   }

   public String getVersionControlSystem() {
      return "CLEARCASE";
   }

   public String getModifiedFlag() {
      return " ";
   }

   public String getLastAuthor() {
      return " ";
   }

   public String getLastModificationDate() {
      return " ";
   }
}
