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

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.ObjectType;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.jdbc.SQL3DataType;

/**
 * @author Roberto E. Escobar
 */
public enum ObjectField {

   branch_type(SQL3DataType.SMALLINT, TableEnum.BRANCH_TABLE, "branch_type"),
   branch_id(true, SQL3DataType.BIGINT, TableEnum.BRANCH_TABLE, "branch_id"),
   branch_name(SQL3DataType.VARCHAR, TableEnum.BRANCH_TABLE, "branch_name"),
   branch_state(SQL3DataType.SMALLINT, TableEnum.BRANCH_TABLE, "branch_state"),
   branch_archive_state(SQL3DataType.SMALLINT, TableEnum.BRANCH_TABLE, "archived"),
   branch_parent_id(SQL3DataType.BIGINT, TableEnum.BRANCH_TABLE, "parent_branch_id"),
   branch_parent_tx_id(SQL3DataType.INTEGER, TableEnum.BRANCH_TABLE, "parent_transaction_id"),
   branch_baseline_tx_id(SQL3DataType.INTEGER, TableEnum.BRANCH_TABLE, "baseline_transaction_id"),
   branch_inherit_access_cntrl(SQL3DataType.SMALLINT, TableEnum.BRANCH_TABLE, "inherit_access_control"),
   branch_associated_art_id(SQL3DataType.INTEGER, TableEnum.BRANCH_TABLE, "associated_art_id"),

   tx_type(SQL3DataType.SMALLINT, TableEnum.TX_DETAILS_TABLE, "tx_type"),
   tx_id(true, SQL3DataType.INTEGER, TableEnum.TXS_TABLE, "transaction_id"),
   tx_current(SQL3DataType.SMALLINT, TableEnum.TXS_TABLE, "tx_current"),
   tx_comment(SQL3DataType.VARCHAR, TableEnum.TX_DETAILS_TABLE, "osee_comment"),
   tx_date(SQL3DataType.TIMESTAMP, TableEnum.TX_DETAILS_TABLE, "time"),
   tx_branch_id(SQL3DataType.BIGINT, TableEnum.TX_DETAILS_TABLE, "branch_id"),
   tx_author_id(SQL3DataType.INTEGER, TableEnum.TX_DETAILS_TABLE, "author"),
   tx_commit_id(SQL3DataType.INTEGER, TableEnum.TX_DETAILS_TABLE, "commit_art_id"),

   art_type(SQL3DataType.BIGINT, TableEnum.ARTIFACT_TABLE, "art_type_id"),
   art_id(true, SQL3DataType.INTEGER, TableEnum.ARTIFACT_TABLE, "art_id"),
   art_gamma_id(true, SQL3DataType.BIGINT, TableEnum.ARTIFACT_TABLE, "gamma_id"),
   art_guid(SQL3DataType.VARCHAR, TableEnum.ARTIFACT_TABLE, "guid"),
   art_mod_type(SQL3DataType.SMALLINT, TableEnum.TXS_TABLE, "mod_type"),
   art_tx_id(true, SQL3DataType.INTEGER, TableEnum.TXS_TABLE, "transaction_id"),
   art_tx_current(SQL3DataType.SMALLINT, TableEnum.TXS_TABLE, "tx_current"),
   art_tx_branch_id(SQL3DataType.BIGINT, TableEnum.TXS_TABLE, "branch_id"),
   art_tx_type(SQL3DataType.SMALLINT, TableEnum.TX_DETAILS_TABLE, "tx_type"),
   art_tx_comment(SQL3DataType.VARCHAR, TableEnum.TX_DETAILS_TABLE, "osee_comment"),
   art_tx_date(SQL3DataType.TIMESTAMP, TableEnum.TX_DETAILS_TABLE, "time"),
   art_tx_author_id(SQL3DataType.INTEGER, TableEnum.TX_DETAILS_TABLE, "author"),
   art_tx_commit_id(SQL3DataType.INTEGER, TableEnum.TX_DETAILS_TABLE, "commit_art_id"),

   attr_type(SQL3DataType.BIGINT, TableEnum.ATTRIBUTE_TABLE, "attr_type_id"),
   attr_id(true, SQL3DataType.INTEGER, TableEnum.ATTRIBUTE_TABLE, "attr_id"),
   attr_gamma_id(true, SQL3DataType.BIGINT, TableEnum.ATTRIBUTE_TABLE, "gamma_id"),
   attr_ds_value(SQL3DataType.VARCHAR, TableEnum.ATTRIBUTE_TABLE, "value"),
   attr_ds_uri(SQL3DataType.VARCHAR, TableEnum.ATTRIBUTE_TABLE, "uri"),
   attr_value(SQL3DataType.VARCHAR, TableEnum.ATTRIBUTE_TABLE, "attr_type_id", "uri", "value"),
   attr_mod_type(SQL3DataType.SMALLINT, TableEnum.TXS_TABLE, "mod_type"),
   attr_tx_id(true, SQL3DataType.INTEGER, TableEnum.TXS_TABLE, "transaction_id"),
   attr_tx_current(SQL3DataType.SMALLINT, TableEnum.TXS_TABLE, "tx_current"),
   attr_tx_branch_id(SQL3DataType.BIGINT, TableEnum.TXS_TABLE, "branch_id"),
   attr_tx_type(SQL3DataType.SMALLINT, TableEnum.TX_DETAILS_TABLE, "tx_type"),
   attr_tx_comment(SQL3DataType.VARCHAR, TableEnum.TX_DETAILS_TABLE, "osee_comment"),
   attr_tx_date(SQL3DataType.TIMESTAMP, TableEnum.TX_DETAILS_TABLE, "time"),
   attr_tx_author_id(SQL3DataType.INTEGER, TableEnum.TX_DETAILS_TABLE, "author"),
   attr_tx_commit_id(SQL3DataType.INTEGER, TableEnum.TX_DETAILS_TABLE, "commit_art_id"),

   rel_type(SQL3DataType.BIGINT, TableEnum.RELATION_TABLE, "rel_link_type_id"),
   rel_id(true, SQL3DataType.INTEGER, TableEnum.RELATION_TABLE, "rel_link_id"),
   rel_gamma_id(true, SQL3DataType.BIGINT, TableEnum.RELATION_TABLE, "gamma_id"),
   rel_rationale(SQL3DataType.VARCHAR, TableEnum.RELATION_TABLE, "rationale"),
   rel_a_art_id(SQL3DataType.INTEGER, TableEnum.RELATION_TABLE, "a_art_id"),
   rel_b_art_id(SQL3DataType.INTEGER, TableEnum.RELATION_TABLE, "b_art_id"),
   rel_mod_type(SQL3DataType.SMALLINT, TableEnum.TXS_TABLE, "mod_type"),
   rel_tx_id(true, SQL3DataType.INTEGER, TableEnum.TXS_TABLE, "transaction_id"),
   rel_tx_current(SQL3DataType.SMALLINT, TableEnum.TXS_TABLE, "tx_current"),
   rel_tx_branch_id(SQL3DataType.BIGINT, TableEnum.TXS_TABLE, "branch_id"),
   rel_tx_type(SQL3DataType.SMALLINT, TableEnum.TX_DETAILS_TABLE, "tx_type"),
   rel_tx_comment(SQL3DataType.VARCHAR, TableEnum.TX_DETAILS_TABLE, "osee_comment"),
   rel_tx_date(SQL3DataType.TIMESTAMP, TableEnum.TX_DETAILS_TABLE, "time"),
   rel_tx_author_id(SQL3DataType.INTEGER, TableEnum.TX_DETAILS_TABLE, "author"),
   rel_tx_commit_id(SQL3DataType.INTEGER, TableEnum.TX_DETAILS_TABLE, "commit_art_id");

   public static enum Family {
      UNDEFINED,
      BRANCH,
      TX,
      ARTIFACT,
      ARTIFACT_TX,
      ATTRIBUTE,
      ATTRIBUTE_TX,
      RELATION,
      RELATION_TX;
   }

   private static SetMultimap<Family, ObjectField> FAMILY_TO_FIELDS;
   private static SetMultimap<Family, ObjectField> FAMILY_TO_REQUIRED_FIELDS;
   private static Map<String, ObjectField> FIELD_BY_NAME;

   private final ObjectType objectType;
   private final Family family;
   private final TableEnum table;
   private final String[] columnName;
   private final boolean primaryKey;
   private final SQL3DataType dataType;

   private ObjectField(SQL3DataType dataType, TableEnum table, String... columnName) {
      this(false, dataType, table, columnName);
   }

   private ObjectField(boolean primaryKey, SQL3DataType dataType, TableEnum table, String... columnName) {
      this.family = family(this);
      this.objectType = objectType(this);
      this.dataType = dataType;
      this.table = table;
      this.columnName = columnName;
      this.primaryKey = primaryKey;
   }

   public boolean isPrimaryKey() {
      return primaryKey;
   }

   public ObjectType getType() {
      return objectType;
   }

   public SQL3DataType getSQLType() {
      return dataType;
   }

   public TableEnum getTable() {
      return table;
   }

   public String[] getColumnNames() {
      return columnName;
   }

   public Family getFamily() {
      return family;
   }

   public boolean isRequired() {
      return isPrimaryKey() || reqByName();
   }

   private boolean reqByName() {
      return isMetaTypeField();
   }

   public boolean isMetaTypeField() {
      boolean result = false;
      String value = this.name();
      if ("art_type".equals(value)) {
         result = true;
      } else if ("attr_type".equals(value)) {
         result = true;
      } else if ("rel_type".equals(value)) {
         result = true;
      }
      return result;
   }

   public boolean isComposite() {
      return getColumnNames().length > 1;
   }

   public static Set<ObjectField> getRequiredFieldsFor(Family family) {
      if (FAMILY_TO_REQUIRED_FIELDS == null) {
         SetMultimap<Family, ObjectField> familyToFields = newSetMultimap();
         for (ObjectField field : ObjectField.values()) {
            if (field.isRequired()) {
               familyToFields.put(field.getFamily(), field);
            }
         }
         ObjectField.FAMILY_TO_REQUIRED_FIELDS = familyToFields;
      }
      return FAMILY_TO_REQUIRED_FIELDS.get(family);
   }

   public static ObjectField fromString(String value) {
      if (FIELD_BY_NAME == null) {
         Map<String, ObjectField> fieldByName = new HashMap<>();
         for (ObjectField field : ObjectField.values()) {
            fieldByName.put(field.name(), field);
         }
         FIELD_BY_NAME = fieldByName;
      }
      return FIELD_BY_NAME.get(value);
   }

   private static ObjectType objectType(ObjectField value) {
      return objectType(value.name());
   }

   public static ObjectType objectType(String value) {
      ObjectType type = ObjectType.UNKNOWN;
      if (value.startsWith("br")) {
         type = ObjectType.BRANCH;
      } else if (value.startsWith("tx")) {
         type = ObjectType.TX;
      } else if (value.startsWith("art")) {
         type = ObjectType.ARTIFACT;
      } else if (value.startsWith("attr")) {
         type = ObjectType.ATTRIBUTE;
      } else if (value.startsWith("rel")) {
         type = ObjectType.RELATION;
      }
      return type;
   }

   private static Family family(ObjectField value) {
      Family family = Family.UNDEFINED;
      String name = value.name();
      if (name.startsWith("br")) {
         family = Family.BRANCH;
      } else if (name.startsWith("tx")) {
         family = Family.TX;
      } else if (name.startsWith("art_tx")) {
         family = Family.ARTIFACT_TX;
      } else if (name.startsWith("attr_tx")) {
         family = Family.ATTRIBUTE_TX;
      } else if (name.startsWith("rel_tx")) {
         family = Family.RELATION_TX;
      } else if (name.startsWith("art")) {
         family = Family.ARTIFACT;
      } else if (name.startsWith("attr")) {
         family = Family.ATTRIBUTE;
      } else if (name.startsWith("rel")) {
         family = Family.RELATION;
      }
      return family;
   }

   public static Set<ObjectField> getFieldsFor(Family family) {
      if (FAMILY_TO_FIELDS == null) {
         SetMultimap<Family, ObjectField> familyToFields = newSetMultimap();
         for (ObjectField field : ObjectField.values()) {
            familyToFields.put(field.getFamily(), field);
         }
         ObjectField.FAMILY_TO_FIELDS = familyToFields;
      }
      return FAMILY_TO_FIELDS.get(family);
   }

   private static <K, V> SetMultimap<K, V> newSetMultimap() {
      Map<K, Collection<V>> map = Maps.newLinkedHashMap();
      return Multimaps.newSetMultimap(map, new Supplier<Set<V>>() {
         @Override
         public Set<V> get() {
            return Sets.newLinkedHashSet();
         }
      });
   }
}