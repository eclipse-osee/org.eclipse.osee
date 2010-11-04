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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.ote.ui.message.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class RunCsvAction extends Action {

   public RunCsvAction() {
      super();
   }

   public RunCsvAction(String text) {
      super(text);
   }

   public RunCsvAction(String text, ImageDescriptor image) {
      super(text, image);
   }

   public RunCsvAction(String text, int style) {
      super(text, style);
   }

   @Override
   public void run() {

      FileDialogSelectionGetter getter = new FileDialogSelectionGetter();

      AWorkbench.getDisplay().syncExec(getter);
      if (getter.getPath() != null) {
         File file = new File(getter.getPath());
         File csvFile = new File(getter.getPath() + ".csv");
         if (!file.exists()) {
            OseeLog.log(Activator.class, Level.SEVERE,
               String.format("[%s] does not exist.  Exiting the csv processor.", file.toString()));
         }

         XMLReader reader;
         try {
            reader = XMLReaderFactory.createXMLReader();
            DetermineElementColumns detElCols = new DetermineElementColumns();
            reader.setContentHandler(detElCols);
            reader.parse(new InputSource(new FileInputStream(file)));
            reader = XMLReaderFactory.createXMLReader();
            ElementVsTimeCSV csv = new ElementVsTimeCSV(detElCols.getElementColumns());
            reader.setContentHandler(csv);
            reader.parse(new InputSource(new FileInputStream(file)));

            Lib.writeBytesToFile(csv.getBuilder().toString().getBytes(), csvFile);
            OseeLog.log(Activator.class, Level.INFO, String.format("Wrote [%s].", csvFile.toString()));
         } catch (SAXException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         } catch (FileNotFoundException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         } catch (IOException ex) {
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
         fd.setFilterExtensions(new String[] {"*.rec"});
         if (fd.open() != null) {
            path = fd.getFilterPath() + File.separator + fd.getFileName();
            //            path = fd.getFileName();
         } else {
            path = null;
         }
      }

      public String getPath() {
         return path;
      }
   }

}
