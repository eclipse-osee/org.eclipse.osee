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
package org.eclipse.osee.framework.ui.data.model.editor.input;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.importing.ExcelOseeTypeDataParser;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditorActivator;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;

/**
 * @author Roberto E. Escobar
 */
public class ExcelXmlODMFileHandler implements IOseeDataTypeHandler {

   public DataTypeSource toODMDataTypeSource(IPath file) throws OseeCoreException {
      OseeDataTypeConverter converter = new OseeDataTypeConverter(new DataTypeSource(file.toPortableString()));
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(FileLocator.openStream(null, file, false));

         ExcelOseeTypeDataParser parser = new ExcelOseeTypeDataParser(converter);
         parser.extractTypesFromSheet(file.toPortableString(), inputStream);

      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               throw new OseeWrappedException(ex);
            }
         }
      }
      return converter.getODMModel();
   }

   public boolean isValid(IPath file) {
      boolean result = false;
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(FileLocator.openStream(null, file, false));
         result = isExcelXML(inputStream);
      } catch (IOException ex) {
         OseeLog.log(ODMEditorActivator.class, Level.SEVERE, ex);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               OseeLog.log(ODMEditorActivator.class, Level.SEVERE, ex);
            }
         }
      }
      return result;
   }

   private boolean isExcelXML(InputStream inputStream) {
      boolean toReturn = false;
      try {
         inputStream.mark(250);
         byte[] buffer = new byte[200];
         int index = 0;
         for (; index < buffer.length; index++) {
            if (inputStream.available() > 0) {
               buffer[index] = (byte) inputStream.read();
            } else {
               break;
            }
         }
         if (index > 0) {
            String header = new String(buffer).toLowerCase();
            if (header.contains("Excel.Sheet") || header.contains("office:excel") || header.contains("<Workbook")) {
               toReturn = true;
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      } finally {
         try {
            inputStream.reset();
         } catch (IOException ex) {
            // Do Nothing
         }
      }
      return toReturn;
   }

}
