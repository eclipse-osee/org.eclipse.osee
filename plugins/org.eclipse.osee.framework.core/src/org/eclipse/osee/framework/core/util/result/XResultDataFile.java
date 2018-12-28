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

package org.eclipse.osee.framework.core.util.result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.internal.Activator;
import org.eclipse.osee.framework.jdk.core.result.IResultDataListener;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

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

   private OutputStreamWriter out;
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

   private File getDirectory() {
      File directory = new File(System.getProperty("java.io.tmpdir"));
      if (!directory.canWrite()) {
         directory = new File(System.getProperty("user.home"));
      }
      return directory;

   }

   @Override
   public void clear() {
      super.clear();
      String filename = String.format("%s.txt", GUID.create());
      file = new File(getDirectory(), filename);
      try {
         out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void addRaw(String str) {
      if (Strings.isValid(str)) {
         try {
            out.write(str);
         } catch (IOException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
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
      String toReturn = null;
      try {
         toReturn = Lib.fileToString(file);
      } catch (IOException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return toReturn;
   }

   @Override
   protected void finalize() throws Throwable {
      dispose();
      super.finalize();
   }

}
