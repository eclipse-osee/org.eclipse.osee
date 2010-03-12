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
package org.eclipse.osee.framework.branch.management.exchange.export;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.branch.management.ExportOptions;
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.database.core.OseeInfo;

/**
 * @author Roberto E. Escobar
 */
public class ManifestExportItem extends AbstractExportItem {
   private final List<AbstractExportItem> exportItems;

   public ManifestExportItem(List<AbstractExportItem> exportItems) {
      super(ExportItem.EXPORT_MANIFEST);
      this.exportItems = exportItems;
   }

   private void addEntry(Appendable appendable, String fileName, int priority, String source) throws IOException {
      ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.ENTRY);
      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.ID, fileName);
      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.PRIORITY, priority);
      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.SOURCE, source);
      ExportImportXml.closePartialXmlNode(appendable);
   }

   @Override
   protected void doWork(Appendable appendable) throws Exception {
      ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.EXPORT_ENTRY);
      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.DATABASE_ID, OseeInfo.getDatabaseGuid());
      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.EXPORT_VERSION, OseeCodeVersion.getVersion());

      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.EXPORT_DATE,
            new Long(new Date().getTime()).toString());
      ExportImportXml.closePartialXmlNode(appendable);

      for (AbstractExportItem relationalItem : exportItems) {
         if (!relationalItem.equals(this)) {
            addEntry(appendable, relationalItem.getFileName(), relationalItem.getPriority(), relationalItem.getSource());
         }
      }
      ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.OPTIONS);
      for (ExportOptions exportOptions : ExportOptions.values()) {
         String value = getOptions().get(exportOptions.name());
         if (exportOptions.equals(ExportOptions.EXCLUDE_BASELINE_TXS)) {
            value = Boolean.valueOf(value).toString();
         }
         ExportImportXml.addXmlAttribute(appendable, exportOptions.name(), value);
      }
      ExportImportXml.closePartialXmlNode(appendable);
   }
}
