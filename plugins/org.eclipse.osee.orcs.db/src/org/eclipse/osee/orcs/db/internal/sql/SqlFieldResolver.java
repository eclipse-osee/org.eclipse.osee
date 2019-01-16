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
package org.eclipse.osee.orcs.db.internal.sql;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.ObjectType;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.orcs.core.ds.DynamicData;
import org.eclipse.osee.orcs.core.ds.DynamicObject;
import org.eclipse.osee.orcs.core.ds.ResultObjectDescription;
import org.eclipse.osee.orcs.core.ds.SelectSet;
import org.eclipse.osee.orcs.db.internal.sql.ObjectField.Family;

/**
 * @author Roberto E. Escobar
 */
public final class SqlFieldResolver {

   private final SqlAliasManager aliasManager;
   private final List<DynamicData> unresolved = new ArrayList<>();
   private final CountingMap<String> counter = new CountingMap<>();
   private final Iterable<SelectSet> selectSets;
   private boolean selectsProcessed;

   public SqlFieldResolver(SqlAliasManager aliasManager, Iterable<SelectSet> selectSets) {
      this.aliasManager = aliasManager;
      this.selectSets = selectSets;
   }

   public void reset() {
      counter.clear();
      unresolved.clear();
      selectsProcessed = false;
   }

   public boolean hasUnresolvedFields() {
      return unresolved != null && !unresolved.isEmpty();
   }

   public Iterable<DynamicData> getUnresolved() {
      return unresolved;
   }

   public Iterable<String> getSortFields() {
      Map<ObjectField, String> fieldToColumn = new LinkedHashMap<>();
      for (SelectSet selectSet : selectSets) {
         DynamicData data = selectSet.getData();
         if (data != null) {
            loadRequiredFields(data, fieldToColumn);
         }
      }

      List<String> toReturn = new ArrayList<>();
      Set<Family> processed = new HashSet<>();
      for (Entry<ObjectField, String> entry : fieldToColumn.entrySet()) {
         ObjectField key = entry.getKey();
         Family family = key.getFamily();
         if (processed.add(family)) {
            for (ObjectField field : ObjectField.getRequiredFieldsFor(family)) {
               String column = fieldToColumn.get(field);
               toReturn.add(column);
            }
         }
      }
      return toReturn;
   }

   private void loadRequiredFields(DynamicData data, Map<ObjectField, String> fieldToColumn) {
      if (data instanceof DynamicObject) {
         DynamicObject object = (DynamicObject) data;
         for (DynamicData child : object.getChildren()) {
            loadRequiredFields(child, fieldToColumn);
         }
      } else {
         ObjectField objectField = getObjectField(data);
         if (objectField != null && objectField.isRequired()) {
            String uniqueId = getColumnUniqueId(data);
            fieldToColumn.put(objectField, uniqueId);
         }
      }
   }

   public ResultObjectDescription getResult() {
      final List<DynamicData> datas = new ArrayList<>();
      for (SelectSet selectSet : selectSets) {
         DynamicData data = selectSet.getData();
         if (data != null) {
            datas.add(data);
         }
      }
      return new ResultObjectDescription() {

         @Override
         public Iterable<? extends DynamicData> getDynamicData() {
            return datas;
         }
      };
   }

   private String getQualifiedName(String alias, String columnName) {
      return String.format("%s.%s", alias, columnName);
   }

   public void resolve() {
      if (!selectsProcessed) {
         selectsProcessed = true;
         processSelects();
      } else {
         processUnresolved();
      }
   }

   private void processSelects() {
      for (SelectSet selectSet : selectSets) {
         DynamicData data = selectSet.getData();
         if (data != null) {
            processData(data);
         }
      }
   }

   private void processData(DynamicData data) {
      if (data instanceof DynamicObject) {
         DynamicObject object = (DynamicObject) data;
         for (DynamicData child : object.getChildren()) {
            processData(child);
         }
         addRequiredFields(object);
      } else {
         processField(data, false);
      }
   }

   private void addRequiredFields(DynamicObject object) {
      Family family = null;
      int level = object.getLevel();
      Set<ObjectField> fields = new HashSet<>();
      for (DynamicData child : object.getChildren()) {
         String fieldId = child.getGuid();
         if (fieldId != null) {
            ObjectField objectField = ObjectField.fromString(fieldId);
            if (objectField != null) {
               family = objectField.getFamily();
               fields.add(objectField);
               level = child.getLevel();
            }
         }
      }
      if (family != null && !fields.isEmpty()) {
         Set<ObjectField> required = ObjectField.getRequiredFieldsFor(family);
         SetView<ObjectField> missing = Sets.difference(required, fields);

         int index = 0;
         for (ObjectField field : missing) {
            String fieldId = field.name();
            DynamicData data = new DynamicData(fieldId, null);
            data.setLevel(level);
            data.setFieldName(fieldId);
            processField(data, true);
            object.addChild(index++, data);
         }
      }
   }

   private void processField(DynamicData data, boolean hidden) {
      String fieldId = data.getGuid();
      if (fieldId != null) {
         ObjectField field = getObjectField(data);
         if (field == null) {
            field = ObjectField.fromString(fieldId);
            setObjectField(data, field);
         }

         int level = data.getLevel();
         data.setPrimaryKey(field.isPrimaryKey());
         data.setHidden(hidden);

         TableEnum table = field.getTable();
         ObjectType type = field.getType();
         String alias = aliasManager.getFirstAlias(level, table, type);

         if (alias != null) {
            resolve(data, field, alias);
         } else {
            unresolved.add(data);
         }
      }
   }

   private void resolve(DynamicData data, ObjectField field, String alias) {
      Map<String, String> columnData = Maps.newLinkedHashMap();

      String[] columnNames = field.getColumnNames();
      for (String columnName : columnNames) {
         String columnUuid;
         if (field.isComposite()) {
            columnUuid = String.format("%s_%s", columnName, nextColumnIndex(columnName));
         } else {
            columnUuid = String.format("%s_%s", field.name(), nextColumnIndex(columnName));
         }
         String qualifiedColumn = getQualifiedName(alias, columnName);
         columnData.put(columnUuid, qualifiedColumn);
      }
      setColumnInfo(data, columnData);
   }

   private int nextColumnIndex(String columnName) {
      counter.put(columnName);
      return counter.get(columnName);
   }

   private void processUnresolved() {
      Iterator<DynamicData> iterator = getUnresolved().iterator();
      while (iterator.hasNext()) {
         DynamicData data = iterator.next();
         ObjectField field = getObjectField(data);
         int level = data.getLevel();

         String alias = aliasManager.getFirstAlias(level, field.getTable(), field.getType());
         if (alias != null) {
            resolve(data, field, alias);
            iterator.remove();
         }
      }
   }

   private static final String OBJECT_FIELD = "sql.object.field";
   private static final String COLUMN_INFO = "sql.column.info";

   public static void setObjectField(DynamicData data, ObjectField field) {
      data.put(OBJECT_FIELD, field);
   }

   public static ObjectField getObjectField(DynamicData data) {
      return data.getObject(OBJECT_FIELD);
   }

   public static void setColumnInfo(DynamicData data, Map<String, String> columnData) {
      data.put(COLUMN_INFO, columnData);
   }

   public static Map<String, String> getColumnInfo(DynamicData data) {
      Map<String, String> columnData = data.getObject(COLUMN_INFO);
      return columnData != null ? columnData : Collections.<String, String> emptyMap();
   }

   public static Collection<String> getColumnUniqueIds(DynamicData data) {
      return getColumnInfo(data).keySet();
   }

   public static String getColumnUniqueId(DynamicData data) {
      return Iterables.getFirst(getColumnUniqueIds(data), null);
   }

}
