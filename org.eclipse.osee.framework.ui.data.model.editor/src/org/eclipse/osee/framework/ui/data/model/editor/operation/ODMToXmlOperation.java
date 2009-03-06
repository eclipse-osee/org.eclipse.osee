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
package org.eclipse.osee.framework.ui.data.model.editor.operation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.xml.ODMXmlWriter;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;

/**
 * @author Roberto E. Escobar
 */
public class ODMToXmlOperation {
   private final String filePath;
   private final ArtifactDataType[] dataTypes;
   private final boolean exportAsSingleFile;

   public ODMToXmlOperation(String filePath, boolean exportAsSingleFile, ArtifactDataType... dataTypes) {
      this.filePath = filePath;
      this.dataTypes = dataTypes;
      this.exportAsSingleFile = exportAsSingleFile;
   }

   public void execute(IProgressMonitor monitor) throws OseeCoreException {
      try {
         monitor.beginTask(String.format("Writing [%s] types to xml [%s]", dataTypes.length, filePath),
               ODMConstants.TOTAL_STEPS);
         if (exportAsSingleFile) {
            writeToSingleFile(monitor);
         } else {
            writeToMultiFiles(monitor);
         }
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      } finally {
         monitor.subTask("");
         monitor.done();
      }
   }

   private void writeToSingleFile(IProgressMonitor monitor) throws Exception {
      File file = new File(filePath);
      File parent = file.getParentFile();
      if (parent != null && !parent.exists()) {
         parent.mkdirs();
      }
      writeXml(monitor, file, dataTypes);
      monitor.worked(ODMConstants.VERY_LONG_TASK);
   }

   private void writeToMultiFiles(IProgressMonitor monitor) throws Exception {
      File directory = new File(filePath);
      if (directory != null && !directory.exists()) {
         directory.mkdirs();
      }
      HashCollection<String, ArtifactDataType> typesByNamespace = new HashCollection<String, ArtifactDataType>();
      for (ArtifactDataType type : dataTypes) {
         typesByNamespace.put(type.getNamespace(), type);
      }
      for (String key : typesByNamespace.keySet()) {
         Collection<ArtifactDataType> types = typesByNamespace.getValues(key);
         if (types != null && !types.isEmpty()) {
            String fileName = getFileName(key);
            monitor.subTask(String.format("Writing [%s] types into [%s]", types.size(), fileName));
            File file = new File(directory, fileName);
            writeXml(monitor, file, types.toArray(new ArtifactDataType[types.size()]));
         }
         monitor.worked(ODMConstants.SHORT_TASK_STEPS);
      }
   }

   private String getFileName(String key) {
      return String.format("osee.types.%s.xml", key);
   }

   private void writeXml(IProgressMonitor listener, File file, ArtifactDataType... types) throws Exception {
      OutputStream outputStream = null;
      ODMXmlWriter xmlWriter = null;
      try {
         if (types != null && types.length > 0) {
            outputStream = new FileOutputStream(file);
            xmlWriter = new ODMXmlWriter(outputStream);
            xmlWriter.write(listener, dataTypes);
         } else {
            throw new Exception(String.format("Data not available - invalid artifact types"));
         }
      } finally {
         try {
            if (xmlWriter != null) {
               xmlWriter.close();
            }
         } finally {
            if (outputStream != null) {
               outputStream.close();
            }
         }
      }
   }
}
