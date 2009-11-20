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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import static org.eclipse.osee.framework.skynet.core.artifact.search.DeprecatedOperator.IS;
import static org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase.ATTRIBUTE_TYPE_TABLE;
import static org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.util.List;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Robert A. Fisher
 */
public class AttributeValueSearch implements ISearchPrimitive {
   private String attributeName;
   private String attributeValue;
   private DeprecatedOperator operator;
   private static final LocalAliasTable ATTRIBUTE_ALIAS_1 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "attr_1");
   private static final LocalAliasTable ATTRIBUTE_ALIAS_2 = new LocalAliasTable(ATTRIBUTE_VERSION_TABLE, "attr_2");
   private static final LocalAliasTable ATTRIBUTE_TYPE_ALIAS_1 =
         new LocalAliasTable(ATTRIBUTE_TYPE_TABLE, "attr_type_1");
   private static final String tables = ATTRIBUTE_ALIAS_1 + "," + ATTRIBUTE_TYPE_ALIAS_1 + "," + TRANSACTIONS_TABLE;
   private final static String TOKEN = ";";

   public AttributeValueSearch(String attributeName) {
      this(attributeName, null, null);
   }

   public AttributeValueSearch(String attributeName, String attributeValue, DeprecatedOperator operator) {

      if (attributeValue == null && operator != null) throw new IllegalArgumentException(
            "An attributeValue must be supplied if an operator is supplied");
      if (attributeValue != null && operator == null) throw new IllegalArgumentException(
            "An operator must be supplied if an attributeValue is supplied");
      if (attributeName == null) throw new IllegalArgumentException("attributeName can not be null");

      this.attributeName = attributeName;

      if (attributeValue != null && attributeValue.length() == 0) {
         this.attributeValue = null;
         this.operator = IS;
      } else {
         this.attributeValue = attributeValue;
         this.operator = operator;
      }
   }

   public String getArtIdColName() {
      return "art_id";
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      String sql;

      if (operator == DeprecatedOperator.LIKE || operator == DeprecatedOperator.CONTAINS)
         sql = ATTRIBUTE_TYPE_ALIAS_1.column("name") + " LIKE ?";
      else
         sql = ATTRIBUTE_TYPE_ALIAS_1.column("name") + "=?";
      dataList.add(attributeName);

      sql +=
            " AND " + ATTRIBUTE_TYPE_ALIAS_1.column("attr_type_id") + "=" + ATTRIBUTE_ALIAS_1.column("attr_type_id") + " AND " + ATTRIBUTE_ALIAS_1.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + "(SELECT max(osee_txs.transaction_id) FROM " + ATTRIBUTE_ALIAS_2 + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + ATTRIBUTE_ALIAS_2.column("attr_id") + "=" + ATTRIBUTE_ALIAS_1.column("attr_id") + " AND " + ATTRIBUTE_ALIAS_2.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?)";

      dataList.add(branch.getId());

      if (attributeValue != null) {
         sql += " AND " + ATTRIBUTE_ALIAS_1.column("value") + operator + " ?";
         if (operator == DeprecatedOperator.CONTAINS)
            dataList.add("%" + attributeValue + "%");
         else
            dataList.add(attributeValue);
      }

      return sql;
   }

   @Override
   public String getTableSql(List<Object> dataList, Branch branch) {
      return tables;
   }

   @Override
   public String toString() {
      return "Attribute value: " + attributeName + operator + "\"" + attributeValue + "\"";
   }

   public String getStorageString() {
      return attributeName + TOKEN + (attributeValue == null ? "" : attributeValue) + TOKEN + (operator == null ? "" : operator.name());
   }

   public static AttributeValueSearch getPrimitive(String storageString) {
      String[] values = storageString.split(TOKEN);
      if (values.length != 3) throw new IllegalStateException(
            "Value for " + AttributeValueSearch.class.getSimpleName() + " not parsable");

      return new AttributeValueSearch(values[0], values[1], DeprecatedOperator.valueOf(values[2]));
   }
}
