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

/**
 * @author Roberto E. Escobar
 */
public class ManifestExportItem extends AbstractExportItem {
   private List<AbstractExportItem> exportItems;

   public ManifestExportItem(int priority, String name, List<AbstractExportItem> exportItems) {
      super(priority, name, "");
      this.exportItems = exportItems;
   }

   private void addEntry(Appendable appendable, String fileName, int priority, String source) throws IOException {
      ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.ENTRY);
      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.ID, fileName);
      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.PRIORITY, priority);
      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.SOURCE, source);
      ExportImportXml.closePartialXmlNode(appendable);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.export.AbstractExportItem#doWork(java.io.File, java.io.Writer, int)
    */
   @Override
   protected void doWork(Appendable appendable) throws Exception {
      ExportImportXml.openPartialXmlNode(appendable, "export");
      ExportImportXml.addXmlAttribute(appendable, "date", new Long(new Date().getTime()).toString());
      ExportImportXml.closePartialXmlNode(appendable);

      for (AbstractExportItem relationalItem : exportItems) {
         if (!relationalItem.equals(this)) {
            addEntry(appendable, relationalItem.getFileName(), relationalItem.getPriority(), relationalItem.getSource());
            if (relationalItem instanceof RelationalExportItemWithType) {
               AbstractExportItem typeItem = ((RelationalExportItemWithType) relationalItem).getTypeItem();
               addEntry(appendable, typeItem.getFileName(), typeItem.getPriority(), typeItem.getSource());
            }
         }
      }
      ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.OPTIONS);
      for (ExportOptions exportOptions : ExportOptions.values()) {
         ExportImportXml.addXmlAttribute(appendable, exportOptions.name(),
               getOptions().getBoolean(exportOptions.name()));
      }
      ExportImportXml.closePartialXmlNode(appendable);
   }
}
