/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.db.internal.loader.processor;

import static org.eclipse.osee.orcs.db.internal.sql.SqlFieldResolver.getObjectField;
import java.sql.Timestamp;
import java.util.Collection;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.DynamicData;
import org.eclipse.osee.orcs.core.ds.DynamicDataHandler;
import org.eclipse.osee.orcs.core.ds.DynamicObject;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.db.internal.proxy.AttributeDataProxyFactory;
import org.eclipse.osee.orcs.db.internal.sql.ObjectField;
import org.eclipse.osee.orcs.db.internal.sql.SqlFieldResolver;

/**
 * @author Roberto E. Escobar
 */
public class DynamicLoadProcessor extends AbstractLoadProcessor<DynamicDataHandler> {

   private final Log logger;
   private final OrcsTokenService tokenService;
   private final AttributeDataProxyFactory proxyFactory;

   public DynamicLoadProcessor(Log logger, OrcsTokenService tokenService, AttributeDataProxyFactory proxyFactory) {
      this.logger = logger;
      this.tokenService = tokenService;
      this.proxyFactory = proxyFactory;
   }

   @Override
   protected Object createPreConditions(Options options) {
      return new DynamicObjectBuilder(logger, tokenService, options);
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
      Object object = null;
      switch (field) {
         case branch_type:
            object = getBranchType(chStmt, columnName);
            break;
         case branch_state:
            object = getBranchState(chStmt, columnName);
            break;
         case branch_archive_state:
            object = getBranchArchivedState(chStmt, columnName);
            break;
         case tx_type:
            object = getTxType(chStmt, columnName);
            break;
         case tx_current:
            object = getTxCurrent(chStmt, columnName);
            break;
         case art_mod_type:
         case attr_mod_type:
         case rel_mod_type:
            object = getModType(chStmt, columnName);
            break;
         default:
            object = getObjectByType(chStmt, field, columnName);
            break;
      }
      return object;
   }

   private Object getObjectByType(JdbcStatement chStmt, ObjectField field, String columnName) {
      Object object;
      Class<?> clazz = field.getSQLType().getJavaEquivalentClass();
      if (clazz.equals(Boolean.class)) {
         object = chStmt.getBoolean(columnName);
      } else if (clazz.equals(Integer.class)) {
         object = chStmt.getInt(columnName);
      } else if (clazz.equals(Long.class)) {
         object = chStmt.getLong(columnName);
      } else if (clazz.equals(Double.class)) {
         object = chStmt.getDouble(columnName);
      } else if (clazz.equals(Timestamp.class)) {
         object = chStmt.getTimestamp(columnName);
      } else if (clazz.equals(String.class)) {
         object = chStmt.getString(columnName);
      } else {
         object = chStmt.getObject(columnName);
      }
      return object;
   }

   private void processCompositeField(JdbcStatement chStmt, DynamicObjectBuilder builder, DynamicData data) {
      Collection<String> columnIds = SqlFieldResolver.getColumnUniqueIds(data);
      builder.onDynamicField(data, data.getName(), getProxyData(chStmt, columnIds));
   }

   private <T> DataProxy<T> getProxyData(JdbcStatement chStmt, Collection<String> columnIds) {
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
      AttributeTypeToken attributeType = tokenService.getAttributeType(chStmt.getLong(typeColumnName));
      String value = chStmt.getString(valueColumnName);
      String uri = chStmt.getString(uriColumnName);
      @SuppressWarnings("unchecked")
      DataProxy<T> dataProxy = (DataProxy<T>) proxyFactory.createProxy(attributeType, value, uri);
      return dataProxy;
   }

   private BranchType getBranchType(JdbcStatement chStmt, String columnName) {
      return BranchType.valueOf(chStmt.getInt(columnName));
   }

   private BranchState getBranchState(JdbcStatement chStmt, String columnName) {
      return BranchState.valueOf(chStmt.getInt(columnName));
   }

   private BranchArchivedState getBranchArchivedState(JdbcStatement chStmt, String columnName) {
      return BranchArchivedState.valueOf(chStmt.getInt(columnName));
   }

   private TransactionDetailsType getTxType(JdbcStatement chStmt, String columnName) {
      return TransactionDetailsType.valueOf(chStmt.getInt(columnName));
   }

   private ModificationType getModType(JdbcStatement chStmt, String columnName) {
      return ModificationType.valueOf(chStmt.getInt(columnName));
   }

   private TxCurrent getTxCurrent(JdbcStatement chStmt, String columnName) {
      return TxCurrent.valueOf(chStmt.getInt(columnName));
   }
}