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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

/**
 * @author Robert A. Fisher
 */
public class AttributeValueSearch implements ISearchPrimitive {
   private final Identity<Long> attributeType;
   private String attributeValue;
   private DeprecatedOperator operator;
   private static final String tables = "osee_attribute attr_1, osee_txs txs";
   private final static String TOKEN = ";";

   public AttributeValueSearch(Identity<Long> attributeType, String attributeValue, DeprecatedOperator operator) {

      if (attributeValue == null && operator != null) {
         throw new IllegalArgumentException("An attributeValue must be supplied if an operator is supplied");
      }
      if (attributeValue != null && operator == null) {
         throw new IllegalArgumentException("An operator must be supplied if an attributeValue is supplied");
      }

      this.attributeType = attributeType;

      if (attributeValue != null && attributeValue.length() == 0) {
         this.attributeValue = null;
         this.operator = IS;
      } else {
         this.attributeValue = attributeValue;
         this.operator = operator;
      }
   }

   @Override
   public String getArtIdColName() {
      return "art_id";
   }

   @Override
   public String getCriteriaSql(List<Object> dataList, IOseeBranch branch) throws OseeCoreException {
      StringBuilder sql = new StringBuilder();

      sql.append(" attr_1.attr_type_id = ? AND attr_1.gamma_id = txs.gamma_id AND txs.tx_current = 1 and txs.branch_id = ?");

      IdentityService remoteIdManager = ServiceUtil.getIdentityService();
      dataList.add(remoteIdManager.getLocalId(attributeType));
      dataList.add(BranchManager.getBranchId(branch));

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
   public String getTableSql(List<Object> dataList, IOseeBranch branch) {
      return tables;
   }

   @Override
   public String toString() {
      return "Attribute value: " + attributeType + operator + "\"" + attributeValue + "\"";
   }

   @Override
   public String getStorageString() {
      return attributeType.getGuid().toString() + TOKEN + (attributeValue == null ? "" : attributeValue) + TOKEN + (operator == null ? "" : operator.name());
   }

   public static AttributeValueSearch getPrimitive(String storageString) {
      String[] values = storageString.split(TOKEN);
      if (values.length != 3) {
         throw new IllegalStateException("Value for " + AttributeValueSearch.class.getSimpleName() + " not parsable");
      }

      Identity<Long> identity = new BaseIdentity<Long>(Long.valueOf(values[0]));
      return new AttributeValueSearch(identity, values[1], DeprecatedOperator.valueOf(values[2]));
   }
}
