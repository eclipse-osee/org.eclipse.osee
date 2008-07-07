/*
 * Created on Jul 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
         "select art_id, value, uri from osee_define_attribute where attr_id = ? and gamma_id =?";

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
                     resultSet.getInt("art_id")));
            }
         }, new Object[] {SQL3DataType.INTEGER, locator.getAttrId(), SQL3DataType.BIGINT, locator.getGamma_id()});
      }
      return attributeData;
   }

   public final static class AttributeData {
      public String value;
      public String uri;
      public int artId;

      public AttributeData(String value, String uri, int artId) {
         super();
         this.value = value;
         this.uri = uri;
         this.artId = artId;
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
