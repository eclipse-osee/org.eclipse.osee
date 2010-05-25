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
import java.util.logging.Level;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.message.internal.Activator;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class RunCSVConversion implements IApplication {

   public Object start(IApplicationContext context) throws Exception {
      String filepath = System.getProperty("csvprocess");

      if (filepath == null) {
         OseeLog.log(
               Activator.class,
               Level.SEVERE,
               "Java Property 'csvprocess' was not set, the program will exit.  Use '-vmargs -Dcsvprocess=<filetoprocess>' ");
         return null;
      }

      File file = new File(filepath);
      File csvFile = new File(filepath + ".csv");
      if (!file.exists()) {
         OseeLog.log(Activator.class, Level.SEVERE, String.format("[%s] does not exist.  Exiting the csv processor.",
               file.toString()));
         return null;
      }

      XMLReader reader = XMLReaderFactory.createXMLReader();
      DetermineElementColumns detElCols = new DetermineElementColumns();
      reader.setContentHandler(detElCols);
      reader.parse(new InputSource(new FileInputStream(file)));

      reader = XMLReaderFactory.createXMLReader();
      ElementVsTimeCSV csv = new ElementVsTimeCSV(detElCols.getElementColumns());
      reader.setContentHandler(csv);
      reader.parse(new InputSource(new FileInputStream(file)));

      Lib.writeBytesToFile(csv.getBuilder().toString().getBytes(), csvFile);
      OseeLog.log(Activator.class, Level.INFO, String.format("Wrote [%s].", csvFile.toString()));
      return IApplication.EXIT_OK;
   }

   public void stop() {
   }

}
