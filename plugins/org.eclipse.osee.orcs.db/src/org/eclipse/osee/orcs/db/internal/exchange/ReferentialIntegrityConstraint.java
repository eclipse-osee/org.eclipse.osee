/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.exchange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.handler.IExportItem;
import org.eclipse.osee.orcs.db.internal.exchange.transform.ExchangeDataProcessor;

/**
 * @author Ryan D. Brooks
 */
public class ReferentialIntegrityConstraint {
   private final List<IExportItem> primaryItems = new ArrayList<>();
   private final List<String> primaryKeys = new ArrayList<>();
   private final List<IExportItem> foreignItems = new ArrayList<>();
   private final List<String[]> foreignKeys = new ArrayList<>();
   private PrimaryKeyCollector collector;
   private Iterator<String> primaryKeyIterator;
   private Iterator<String[]> foreignKeyIterator;

   public ReferentialIntegrityConstraint(IExportItem primaryItem, String primaryKey) {
      addPrimaryKey(primaryItem, primaryKey);
   }

   public void addPrimaryKey(IExportItem primaryItem, String primaryKey) {
      primaryItems.add(primaryItem);
      primaryKeys.add(primaryKey);
   }

   public void addForeignKey(IExportItem foreignItem, String... foreignKeys) {
      foreignItems.add(foreignItem);
      this.foreignKeys.add(foreignKeys);
   }

   public List<IExportItem> getPrimaryItems() {
      primaryKeyIterator = primaryKeys.iterator();
      return primaryItems;
   }

   public String getPrimaryKeyListing() {
      List<String> primaryKeys = new ArrayList<>(primaryItems.size());
      for (IExportItem primaryTable : getPrimaryItems()) {
         primaryKeys.add(primaryTable + "." + getPrimaryKey());
      }
      return Collections.toString(",", primaryKeys);
   }

   /**
    * @return next primary key from the iterator that was initialized when getPrimaryItems was last called
    */
   public String getPrimaryKey() {
      return primaryKeyIterator.next();
   }

   public List<IExportItem> getForeignItems() {
      foreignKeyIterator = foreignKeys.iterator();
      return foreignItems;
   }

   /**
    * @return next set of foreign keys from the iterator that was initialized when getForeignItems was last called
    */
   public String[] getForeignKeys() {
      return foreignKeyIterator.next();
   }

   public HashCollection<String, Long> getMissingPrimaryKeys() {
      return collector.getMissingPrimaryKeys();
   }

   public Set<Long> getUnreferencedPrimaryKeys() {
      return collector.getUnreferencedPrimaryKeys();
   }

   public void checkConstraint(Log logger, JdbcClient jdbcClient, ExchangeDataProcessor processor)  {
      collector = new PrimaryKeyCollector(logger, jdbcClient);

      for (IExportItem primaryTable : getPrimaryItems()) {
         collector.setPrimaryKey(getPrimaryKey());
         processor.parse(primaryTable, collector);
      }
      for (IExportItem foreignTable : getForeignItems()) {
         ForeignKeyReader foreignKeyReader =
            new ForeignKeyReader(logger, jdbcClient, collector, foreignTable, getForeignKeys());
         processor.parse(foreignTable, foreignKeyReader);
      }
   }
}
