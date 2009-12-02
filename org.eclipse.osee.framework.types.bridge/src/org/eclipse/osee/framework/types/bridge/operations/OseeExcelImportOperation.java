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
package org.eclipse.osee.framework.types.bridge.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.osee.ModelUtil;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.skynet.core.importing.ExcelOseeTypeDataParser;
import org.eclipse.osee.framework.types.bridge.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class OseeExcelImportOperation extends AbstractOperation {
   private final File sourceFile;
   private final Map<String, OseeTypeModel> models;
   private final File destinationFile;

   public OseeExcelImportOperation(File sourceFile, File destinationFile) {
      super("Importing OSEE Types", Activator.PLUGIN_ID);
      this.sourceFile = sourceFile;
      this.destinationFile = destinationFile;
      this.models = new HashMap<String, OseeTypeModel>();
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      ExcelToEMFModel converter = new ExcelToEMFModel(models);
      ExcelOseeTypeDataParser importer = new ExcelOseeTypeDataParser(converter, false);
      converter.createModel(destinationFile.getName());

      if (sourceFile.isFile()) {
         monitor.setTaskName("Importing " + sourceFile.getName());
         importer.extractTypesFromSheet(sourceFile.getName(), new FileInputStream(sourceFile));
      } else if (sourceFile.isDirectory()) {
         File[] children = sourceFile.listFiles(new ExcelFilter());
         monitor.beginTask("Importing files", children.length + 1);
         for (File childFile : children) {
            checkForCancelledStatus(monitor);

            monitor.subTask(childFile.getName());
            importer.extractTypesFromSheet(childFile.getName(), new FileInputStream(childFile));
         }
      }
      checkForCancelledStatus(monitor);
      monitor.setTaskName("Finalizing");
      importer.finish();

      if (destinationFile.isDirectory()) {
         destinationFile.mkdirs();
      }

      for (Entry<String, OseeTypeModel> entry : models.entrySet()) {
         URI target;
         if (destinationFile.isDirectory()) {
            target = new File(destinationFile, entry.getKey() + ".osee").toURI();
         } else {
            target = destinationFile.toURI();
         }

         ModelUtil.saveModel(target, entry.getValue());
      }
   }
   private final class ExcelFilter implements FilenameFilter {

      @Override
      public boolean accept(File dir, String name) {
         return dir.isDirectory() || dir.isFile() && Lib.getExtension(name).endsWith("xml");
      }
   }

}