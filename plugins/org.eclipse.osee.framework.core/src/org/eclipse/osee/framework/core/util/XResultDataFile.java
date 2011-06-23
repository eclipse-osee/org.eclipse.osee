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

package org.eclipse.osee.framework.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Used to log Info, Warning and Errors to multiple locations (logger, stderr/out and XResultView). Upon completion, a
 * call to report(title) will open results in the ResultsView<br/>
 * <br/>
 * Used for large results cause uses file instead of StringBuffer<br/>
 * <br/>
 * Call dispose() after to close and remove file.
 * 
 * @author Donald G. Dunne
 */
public final class XResultDataFile extends XResultData {

   OutputStreamWriter out = null;
   private File file;

   public XResultDataFile() {
      this(true, (IResultDataListener[]) null);
   }

   public XResultDataFile(boolean enableOseeLog) {
      this(enableOseeLog, (IResultDataListener[]) null);
   }

   public XResultDataFile(boolean enableOseeLog, IResultDataListener... listeners) {
      super(enableOseeLog, listeners);
      clear();
   }

   @Override
   public void clear() {
      super.clear();
      String filename = System.getProperty("java.io.tmpdir") + GUID.create() + ".txt";
      file = new File(filename);
      try {
         out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
      } catch (Exception ex) {
         System.out.println("Execption - " + ex.getLocalizedMessage());
      }
   }

   @Override
   public void addRaw(String str) {
      char[] chars = str.toCharArray();
      try {
         out.write(chars, 0, chars.length);
      } catch (IOException ex) {
         System.out.println("Execption - " + ex.getLocalizedMessage());
      }
   }

   @Override
   public void dispose() {
      Lib.close(out);
      if (file != null && file.exists()) {
         file.delete();
      }
   }

   @Override
   public String toString() {
      Lib.close(out);
      try {
         return Lib.fileToString(file);
      } catch (IOException ex) {
         System.out.println("Execption - " + ex.getLocalizedMessage());
      }
      return null;
   }

}
