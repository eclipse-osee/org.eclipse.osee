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
import java.util.List;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Robert A. Fisher
 */
public class AttributeValueSearch implements ISearchPrimitive {
   private final String attributeName;
   private String attributeValue;
   private DeprecatedOperator operator;
   private static final String tables = "osee_attribute attr_1, osee_attribute_type attr_type_1, osee_txs txs";
   private final static String TOKEN = ";";

   public AttributeValueSearch(String attributeName) {
      this(attributeName, null, null);
   }

   public AttributeValueSearch(String attributeName, String attributeValue, DeprecatedOperator operator) {

      if (attributeValue == null && operator != null) {
         throw new IllegalArgumentException("An attributeValue must be supplied if an operator is supplied");
      }
      if (attributeValue != null && operator == null) {
         throw new IllegalArgumentException("An operator must be supplied if an attributeValue is supplied");
      }
      if (attributeName == null) {
         throw new IllegalArgumentException("attributeName can not be null");
      }

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
      StringBuilder sql = new StringBuilder();

      if (operator == DeprecatedOperator.LIKE || operator == DeprecatedOperator.CONTAINS) {
         sql.append("attr_type_1.name LIKE ?");
      } else {
         sql.append("attr_type_1.name = ?");
      }
      dataList.add(attributeName);

      sql.append(" AND attr_type_1.attr_type_id = attr_1.attr_type_id AND attr_1.gamma_id = txs.gamma_id AND txs.tx_current = 1 and txs.branch_id = ?");

      dataList.add(branch.getId());

      if (attributeValue != null) {
         sql.append(" AND attr_1.value ");
         sql.append(operator);
         sql.append(" ?");
         if (operator == DeprecatedOperator.CONTAINS) {
            dataList.add("%" + attributeValue + "%");
         } else {
            dataList.add(attributeValue);
         }
      }
      return sql.toString();
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
      if (values.length != 3) {
         throw new IllegalStateException("Value for " + AttributeValueSearch.class.getSimpleName() + " not parsable");
      }

      return new AttributeValueSearch(values[0], values[1], DeprecatedOperator.valueOf(values[2]));
   }
}
