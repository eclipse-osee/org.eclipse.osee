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

import java.io.File;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class RelationalExportItemWithType extends RelationalExportItem {

   private final RelationalExportItem typeExportItem;
   private final ColumnIdCollector typeCollector;

   public RelationalExportItemWithType(int priority, String name, String source, String typeColumn, String regularQuery, String typeQuery) {
      super(priority, name, source, regularQuery);
      this.typeCollector = new ColumnIdCollector(typeColumn);
      this.typeExportItem = new RelationalExportItem(priority * -1, name + ".type", getSource() + "_type", typeQuery);
   }

   @Override
   public void setOptions(Options options) {
      super.setOptions(options);
      this.typeExportItem.setOptions(options);
   }

   @Override
   public void setWriteLocation(File writeLocation) {
      super.setWriteLocation(writeLocation);
      this.typeExportItem.setWriteLocation(writeLocation);
   }

   @Override
   public void cleanUp() {
      this.typeExportItem.cleanUp();
      try {
         this.typeCollector.cleanUp();
      } catch (OseeDataStoreException ex) {
         notifyOnExportException(ex);
      }
      super.cleanUp();
   }

   public AbstractExportItem getTypeItem() {
      return typeExportItem;
   }

   @Override
   protected void doWork(Appendable appendable) throws Exception {
      this.typeCollector.initialize();
      this.addExportColumnListener(typeCollector);

      super.doWork(appendable);

      this.removeExportColumnListener(typeCollector);
      this.typeCollector.store();
      this.typeExportItem.setConnection(getConnection());
      this.typeExportItem.setOptions(getOptions());
      this.typeExportItem.setJoinQueryId(typeCollector.getQueryId());
      this.typeExportItem.run();
   }
}
