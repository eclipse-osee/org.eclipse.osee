/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.processor;

import static org.eclipse.osee.orcs.db.internal.sql.SqlFieldResolver.getObjectField;
import java.util.Collection;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.DynamicData;
import org.eclipse.osee.orcs.core.ds.DynamicDataHandler;
import org.eclipse.osee.orcs.core.ds.DynamicObject;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.db.internal.loader.ProxyDataFactory;
import org.eclipse.osee.orcs.db.internal.sql.ObjectField;
import org.eclipse.osee.orcs.db.internal.sql.SqlFieldResolver;

/**
 * @author Roberto E. Escobar
 */
public class DynamicLoadProcessor extends AbstractLoadProcessor<DynamicDataHandler> {

   private final Log logger;
   private final OrcsTypes orcsTypes;
   private final ProxyDataFactory proxyFactory;

   public DynamicLoadProcessor(Log logger, OrcsTypes orcsTypes, ProxyDataFactory proxyFactory) {
      super();
      this.logger = logger;
      this.orcsTypes = orcsTypes;
      this.proxyFactory = proxyFactory;
   }

   @Override
   protected Object createPreConditions(Options options) {
      return new DynamicObjectBuilder(logger, orcsTypes, options);
   }

   private DynamicObjectBuilder getBuilder(DynamicDataHandler handler, Object conditions) {
      DynamicObjectBuilder builder = (DynamicObjectBuilder) conditions;
      builder.setHandler(handler);
      return builder;
   }

   @Override
   protected void onRow(DynamicDataHandler handler, JdbcStatement chStmt, Options options, Object conditions) {
      DynamicObjectBuilder builder = getBuilder(handler, conditions);
      for (DynamicData data : builder.getDescriptors()) {
         processData(chStmt, builder, data);
      }
   }

   @Override
   protected void onEnd(DynamicDataHandler handler, Options options, Object conditions, int rowCount) {
      if (rowCount > 0) {
         getBuilder(handler, conditions).onEnd();
      }
   }

   private void processData(JdbcStatement chStmt, DynamicObjectBuilder builder, DynamicData data) {
      if (data instanceof DynamicObject) {
         DynamicObject dynamicObject = (DynamicObject) data;
         builder.onDynamicObjectStart(dynamicObject);
         for (DynamicData child : dynamicObject.getChildren()) {
            processData(chStmt, builder, child);
         }
         builder.onDynamicObjectEnd(dynamicObject);
      } else {
         ObjectField field = getObjectField(data);
         if (field.isComposite()) {
            processCompositeField(chStmt, builder, data);
         } else {
            String uniqueId = SqlFieldResolver.getColumnUniqueId(data);
            Object value = getValue(chStmt, field, uniqueId);
            builder.onDynamicField(data, data.getName(), value);
         }
      }
   }

   private Object getValue(JdbcStatement chStmt, ObjectField field, String columnName) {
      Object object = chStmt.getObject(columnName);
      switch (field) {
         case branch_type:
            object = getBranchType(object);
            break;
         case branch_state:
            object = getBranchState(object);
            break;
         case branch_archive_state:
            object = getBranchArchivedState(object);
            break;
         case tx_type:
            object = getTxType(object);
            break;
         case tx_current:
            object = getTxCurrent(object);
            break;
         case art_mod_type:
         case attr_mod_type:
         case rel_mod_type:
            object = getModType(object);
            break;
         default:
            break;
      }
      return object;
   }

   private void processCompositeField(JdbcStatement chStmt, DynamicObjectBuilder builder, DynamicData data) {
      Collection<String> columnIds = SqlFieldResolver.getColumnUniqueIds(data);
      Object value = getProxyData(chStmt, columnIds);

      builder.onDynamicField(data, data.getName(), value);
   }

   private DataProxy getProxyData(JdbcStatement chStmt, Collection<String> columnIds) {
      String typeColumnName = null;
      String uriColumnName = null;
      String valueColumnName = null;
      for (String id : columnIds) {
         if (id.contains("uri")) {
            uriColumnName = id;
         } else if (id.contains("type")) {
            typeColumnName = id;
         } else if (id.contains("value")) {
            valueColumnName = id;
         }
      }
      long typeUuid = chStmt.getLong(typeColumnName);
      String value = chStmt.getString(valueColumnName);
      String uri = chStmt.getString(uriColumnName);
      return proxyFactory.createProxy(typeUuid, value, uri);
   }

   private BranchType getBranchType(Object object) {
      return BranchType.valueOf((Integer) object);
   }

   private BranchState getBranchState(Object object) {
      return BranchState.getBranchState((Integer) object);
   }

   private BranchArchivedState getBranchArchivedState(Object object) {
      return BranchArchivedState.valueOf((Integer) object);
   }

   private TransactionDetailsType getTxType(Object object) {
      return TransactionDetailsType.toEnum((Integer) object);
   }

   private ModificationType getModType(Object object) {
      return ModificationType.getMod((Integer) object);
   }

   private TxChange getTxCurrent(Object object) {
      return TxChange.getChangeType((Integer) object);
   }

}