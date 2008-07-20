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
package org.eclipse.osee.framework.search.engine.attribute;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.search.engine.data.IAttributeLocator;
import org.eclipse.osee.framework.search.engine.utility.DatabaseUtil;
import org.eclipse.osee.framework.search.engine.utility.IRowProcessor;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataStore {
   private static AttributeDataStore instance = null;

   private static final String SELECT_ATTRIBUTE =
         "SELECT attr1.art_id, attr1.gamma_id, attr1.value, attr1.uri, attrtype.tagger_id, txd1.branch_id FROM osee_define_attribute attr1, osee_define_attribute_type attrtype, osee_define_txs txs1, osee_define_tx_details txd1 WHERE attr1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND attrtype.attr_type_id = attr1.attr_type_id AND attr1.gamma_id = ?";

   private static final String RESTRICT_BRANCH = " AND txd1.branch_id = ?";

   private AttributeDataStore() {
   }

   public static AttributeDataStore getInstance() {
      if (instance == null) {
         instance = new AttributeDataStore();
      }
      return instance;
   }

   public List<AttributeData> getAttributes(int branchId, Collection<IAttributeLocator> locators) throws Exception {
      return getAttributes(branchId, locators.toArray(new IAttributeLocator[locators.size()]));
   }

   public AttributeData getAttribute(final long gammaId) throws Exception {
      int branchId = -1;
      List<AttributeData> result = getAttributes(branchId, new IAttributeLocator() {

         @Override
         public int getAttrId() {
            return -1;
         }

         @Override
         public long getGammaId() {
            return gammaId;
         }
      });
      return result.size() > 0 ? result.get(0) : null;
   }

   public List<AttributeData> getAttributes(int branchId, IAttributeLocator... locators) throws Exception {
      final List<AttributeData> attributeData = new ArrayList<AttributeData>();
      String sqlQuery = SELECT_ATTRIBUTE;
      int dataSize = 2;
      if (branchId > -1) {
         sqlQuery += RESTRICT_BRANCH;
         dataSize = 4;
      }
      for (IAttributeLocator locator : locators) {
         Object[] data = new Object[dataSize];
         data[0] = SQL3DataType.BIGINT;
         data[1] = locator.getGammaId();
         if (branchId > -1) {
            data[2] = SQL3DataType.INTEGER;
            data[3] = branchId;
         }
         DatabaseUtil.executeQuery(sqlQuery, new IRowProcessor() {
            @Override
            public void processRow(ResultSet resultSet) throws Exception {
               attributeData.add(new AttributeData(resultSet.getInt("art_id"), resultSet.getLong("gamma_id"),
                     resultSet.getInt("branch_id"), resultSet.getString("value"), resultSet.getString("uri"),
                     resultSet.getString("tagger_id")));
            }
         }, data);
      }
      return attributeData;
   }
}
