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
package org.eclipse.osee.orcs.db.internal.exchange.export;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ExportOptions;
import org.eclipse.osee.orcs.SystemProperties;
import org.eclipse.osee.orcs.db.internal.exchange.ExportImportXml;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;

/**
 * @author Roberto E. Escobar
 */
public class ManifestExportItem extends AbstractXmlExportItem {

   private final SystemProperties preferences;
   private final List<AbstractExportItem> exportItems;
   private final PropertyStore options;

   public ManifestExportItem(Log logger, SystemProperties preferences, List<AbstractExportItem> exportItems, PropertyStore options) {
      super(logger, ExportItem.EXPORT_MANIFEST);
      this.preferences = preferences;
      this.exportItems = exportItems;
      this.options = options;
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
      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.DATABASE_ID, preferences.getSystemUuid());
      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.EXPORT_VERSION, OseeCodeVersion.getBundleVersion());

      ExportImportXml.addXmlAttribute(appendable, ExportImportXml.EXPORT_DATE,
         new Long(new Date().getTime()).toString());
      ExportImportXml.closePartialXmlNode(appendable);

      for (AbstractExportItem relationalItem : exportItems) {
         if (!relationalItem.equals(this)) {
            addEntry(appendable, relationalItem.getFileName(), relationalItem.getPriority(),
               relationalItem.getSource());
         }
      }
      ExportImportXml.openPartialXmlNode(appendable, ExportImportXml.OPTIONS);
      for (ExportOptions exportOption : ExportOptions.values()) {
         String value = options.get(exportOption.name());
         ExportImportXml.addXmlAttribute(appendable, exportOption.name(), value);
      }
      ExportImportXml.closePartialXmlNode(appendable);
   }
}