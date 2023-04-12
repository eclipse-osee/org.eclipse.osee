/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.exchange.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.db.internal.exchange.ExportTableConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Roberto E. Escobar
 */
public class ManifestSaxHandler extends BaseExportImportSaxHandler {

   private final List<IExportItem> filesToImport;
   private String typeModelFile;
   private String metadataFile;
   private IExportItem branchFile;
   private String sourceDatabaseId;
   private Date sourceExportDate;
   private String exportVersion;

   public ManifestSaxHandler() {
      super();
      this.filesToImport = new ArrayList<>();
      this.typeModelFile = null;
      this.metadataFile = null;
      this.branchFile = null;
      this.sourceExportDate = null;
      this.sourceDatabaseId = "UNKNOWN";
   }

   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
      if (localName.equalsIgnoreCase(ExportTableConstants.EXPORT_ENTRY)) {
         sourceDatabaseId = attributes.getValue(ExportTableConstants.DATABASE_ID);
         sourceExportDate = new Date(Long.parseLong(attributes.getValue(ExportTableConstants.EXPORT_DATE)));
         exportVersion = attributes.getValue(ExportTableConstants.EXPORT_VERSION);
      }
      super.startElementFound(uri, localName, name, attributes);
   }

   @Override
   protected void processData(Map<String, String> fieldMap) {
      String fileName = fieldMap.get(ExportTableConstants.ID);
      Integer priority = Integer.valueOf(fieldMap.get(ExportTableConstants.PRIORITY));
      String source = fieldMap.get(ExportTableConstants.SOURCE);

      if (Strings.isValid(fileName) && Strings.isValid(source)) {
         if (source.equals(ExportTableConstants.TYPE_MODEL)) {
            typeModelFile = fileName;
         } else if (source.equals(ExportTableConstants.DB_SCHEMA)) {
            this.metadataFile = fileName;
         } else {
            ImportFile importFile = new ImportFile(fileName, source, priority);
            if (source.equals("osee_branch")) {
               branchFile = importFile;
            } else {
               filesToImport.add(importFile);
            }
         }
      }
   }

   public IExportItem getBranchFile() {
      return branchFile;
   }

   public String getMetadataFile() {
      return Strings.isValid(metadataFile) ? metadataFile : "";
   }

   public String getSourceDatabaseId() {
      return sourceDatabaseId;
   }

   public Date getSourceExportDate() {
      return sourceExportDate;
   }

   public String getSourceExportVersion() {
      return exportVersion;
   }

   public List<IExportItem> getImportFiles() {
      Collections.sort(filesToImport, new Comparator<IExportItem>() {
         @Override
         public int compare(IExportItem item1, IExportItem item2) {
            return item1.getPriority() - item2.getPriority();
         }

      });
      return filesToImport;
   }

   public String getTypeModel() {
      return typeModelFile;
   }

   public class ImportFile implements IExportItem {
      private final String fileName;
      private final String source;
      private final int priority;

      public ImportFile(String fileName, String source, int priority) {
         this.fileName = fileName;
         this.source = source;
         this.priority = priority;
      }

      @Override
      public String getFileName() {
         return fileName;
      }

      @Override
      public int getPriority() {
         return priority;
      }

      @Override
      public String getSource() {
         return source;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         }
         if (!(obj instanceof IExportItem)) {
            return false;
         }
         IExportItem other = (IExportItem) obj;
         return getSource().equals(other.getSource());
      }

      @Override
      public int hashCode() {
         return 37 * priority;
      }

      @Override
      public String toString() {
         return String.format("ImportFile [fileName=%s, source=%s, priority=%d]", fileName, source, priority);
      }
   }

}