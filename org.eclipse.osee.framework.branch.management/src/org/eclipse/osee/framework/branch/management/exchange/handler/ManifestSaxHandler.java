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
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ManifestSaxHandler extends BaseExportImportSaxHandler {

   private List<ImportFile> filesToImport;
   private List<ImportFile> typesToCheck;
   private String metadataFile;
   private ImportFile branchFile;

   public ManifestSaxHandler() {
      super();
      this.filesToImport = new ArrayList<ImportFile>();
      this.typesToCheck = new ArrayList<ImportFile>();
      this.metadataFile = null;
      this.branchFile = null;
   }

   @Override
   protected void processData(Map<String, String> fieldMap) {
      String fileName = fieldMap.get(ExportImportXml.ID);
      Integer priority = new Integer(fieldMap.get(ExportImportXml.PRIORITY));
      String source = fieldMap.get(ExportImportXml.SOURCE);

      if (Strings.isValid(fileName) && Strings.isValid(source) && priority != null) {
         if (source.equals(ExportImportXml.DB_SCHEMA)) {
            this.metadataFile = fileName;
         } else {
            if (priority > 0) {
               ImportFile importFile = new ImportFile(fileName, source, priority);
               if (source.equals(SkynetDatabase.BRANCH_TABLE.toString().toLowerCase())) {
                  branchFile = importFile;
               } else {
                  filesToImport.add(importFile);
               }
            } else {
               typesToCheck.add(new ImportFile(fileName, source, priority));
            }

         }
      }
   }

   public ImportFile getBranchFile() {
      return branchFile;
   }

   public String getMetadataFile() {
      return Strings.isValid(metadataFile) ? metadataFile : "";
   }

   public List<ImportFile> getImportFiles() {
      Collections.sort(filesToImport);
      return filesToImport;
   }

   public List<ImportFile> getTypeFiles() {
      Collections.sort(typesToCheck);
      return typesToCheck;
   }

   public class ImportFile implements Comparable<ImportFile> {
      private String fileName;
      private String source;
      private Integer priority;

      public ImportFile(String fileName, String source, Integer priority) {
         this.fileName = fileName;
         this.source = source;
         this.priority = priority;
      }

      public String getFileName() {
         return fileName;
      }

      public Integer getPriority() {
         return priority;
      }

      public String getSource() {
         return source;
      }

      /* (non-Javadoc)
       * @see java.lang.Object#equals(java.lang.Object)
       */
      @Override
      public boolean equals(Object obj) {
         if (obj == this) return true;
         if (!(obj instanceof ImportFile)) return false;
         ImportFile other = (ImportFile) obj;
         return this.priority == other.priority && this.fileName.equals(other.fileName);
      }

      /* (non-Javadoc)
       * @see java.lang.Object#hashCode()
       */
      @Override
      public int hashCode() {
         return (int) (37 * priority);
      }

      /* (non-Javadoc)
       * @see java.lang.Comparable#compareTo(java.lang.Object)
       */
      @Override
      public int compareTo(ImportFile other) {
         return this.priority.compareTo(other.priority);
      }
   }
}
