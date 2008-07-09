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
package org.eclipse.osee.framework.search.engine.utility;

import java.net.URI;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.search.engine.data.IAttributeLocator;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataStore {

   private static final String SELECT_ATTRIBUTE =
         "select attr1.art_id, attr1.value, attr1.uri, txd1.branch_id from osee_define_attribute attr1, osee_define_txs txs1, osee_define_tx_details txd1 where attr1.gamma_id = txs1.gamma_id and txs1.transaction_id = txd1.transaction_id and attr1.attr_id = ? and attr1.gamma_id =?";

   public static List<AttributeData> getAttribute(Collection<IAttributeLocator> locators) throws Exception {
      return getAttribute(locators.toArray(new IAttributeLocator[locators.size()]));
   }

   public static List<AttributeData> getAttribute(IAttributeLocator... locators) throws Exception {
      final List<AttributeData> attributeData = new ArrayList<AttributeData>();
      for (IAttributeLocator locator : locators) {
         DatabaseUtil.executeQuery(SELECT_ATTRIBUTE, new IRowProcessor() {
            @Override
            public void processRow(ResultSet resultSet) throws Exception {
               attributeData.add(new AttributeData(resultSet.getString("value"), resultSet.getString("uri"),
                     resultSet.getInt("art_id"), resultSet.getInt("branch_id")));
            }
         }, new Object[] {SQL3DataType.INTEGER, locator.getAttrId(), SQL3DataType.BIGINT, locator.getGammaId()});
      }
      return attributeData;
   }

   public final static class AttributeData {
      private String value;
      private String uri;
      private int artId;
      private int branchId;

      public AttributeData(String value, String uri, int artId, int branchId) {
         super();
         this.value = value;
         this.uri = uri;
         this.artId = artId;
         this.branchId = branchId;
      }

      public String getValue() {
         return value;
      }

      public String getUri() {
         return uri;
      }

      public int getArtId() {
         return artId;
      }

      public int getBranchId() {
         return branchId;
      }

      public boolean isUriValid() {
         boolean toReturn = false;
         try {
            String value = getUri();
            if (value != null && value.length() > 0) {
               URI uri = new URI(value);
               if (uri != null) {
                  toReturn = true;
               }
            }
         } catch (Exception ex) {
            // DO NOTHING
         }
         return toReturn;
      }

   }
}
