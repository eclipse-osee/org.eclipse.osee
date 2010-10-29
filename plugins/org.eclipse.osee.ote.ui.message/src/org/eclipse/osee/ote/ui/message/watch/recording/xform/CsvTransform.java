/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.watch.recording.xform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.ote.ui.message.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

/**
 * @author Andrew M. Finkbeiner
 */
public class CsvTransform extends Action {
   @Override
   public void run() {

      FileDialogSelectionGetter getter = new FileDialogSelectionGetter();

      AWorkbench.getDisplay().syncExec(getter);
      if (getter.getPath() != null) {
         File file = new File(getter.getPath());
         File csvFile = new File(getter.getPath() + "_transformed.csv");
         if (!file.exists()) {
            OseeLog.log(Activator.class, Level.SEVERE,
               String.format("[%s] does not exist.  Exiting the csv transformer.", file.toString()));
         }

         try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            LinkedHashSet<String> columns = new LinkedHashSet<String>();

            String line;
            while ((line = br.readLine()) != null) {
               String[] items = line.split(",");
               if (items.length <= 3) {
                  continue;
               }
               String msg = items[1];
               msg += ".";
               for (int i = 3; i < items.length; i += 2) {
                  columns.add(msg + items[i]);
               }
            }
            br.close();

            String[] columnsArray = columns.toArray(new String[columns.size()]);

            BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));
            bw.write("time,");
            if (columnsArray.length >= 254) {
               OseeLog.log(CsvTransform.class, Level.SEVERE, String.format(
                  "%d columns have been lost in the CsvTransform because there is a limitation of 256 rows in excel.",
                  (columnsArray.length - 254)));
            }
            for (int i = 0; i < columnsArray.length && i < 254; i++) {
               bw.write(columnsArray[i]);
               bw.write(",");
            }
            bw.write("\n");

            br = new BufferedReader(new FileReader(file));
            Map<String, String> values = new HashMap<String, String>();
            while ((line = br.readLine()) != null) {
               values.clear();
               String[] items = line.split(",");
               if (items.length <= 3) {
                  continue;
               }
               String time = items[0];
               String msg = items[1];
               msg += ".";
               for (int i = 3; i < items.length; i += 2) {
                  String value = null;
                  if (i + 1 >= items.length) {
                     value = "null";
                  } else {
                     value = items[i + 1];
                  }
                  values.put(msg + items[i], value);
               }
               bw.write(time);
               bw.write(",");
               for (int i = 0; i < columnsArray.length && i < 254; i++) {
                  String value = values.get(columnsArray[i]);
                  if (value != null) {
                     bw.write(value);
                  }
                  bw.write(",");
               }
               bw.write("\n");
            }
            br.close();
            bw.flush();
            bw.close();

            OseeLog.log(Activator.class, Level.INFO, String.format("Wrote [%s].", csvFile.toString()));
         } catch (Throwable ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } else {
         OseeLog.log(Activator.class, Level.INFO, "No file was selected for translating.");
      }
   }

   private static final class FileDialogSelectionGetter implements Runnable {

      String path;

      @Override
      public void run() {
         FileDialog fd = new FileDialog(AWorkbench.getActiveShell(), SWT.OPEN);
         fd.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());
         // fd.setFilterExtensions(new String[]{"*.rec"});
         if (fd.open() != null) {
            path = fd.getFilterPath() + File.separator + fd.getFileName();
            // path = fd.getFileName();
         } else {
            path = null;
         }
      }

      public String getPath() {
         return path;
      }
   }
}
