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

import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;

/**
 * @author Ryan D. Brooks
 */
public class AttributeValueCriteria extends AbstractArtifactSearchCriteria {
   private DynamicAttributeDescriptor attributeType;
   private String value;
   private String txsAlias;
   private String txdAlias;
   private String attrAlias;

   /**
    * Constructor for search criteria that finds an attribute of the given type with its current value equal to the
    * given value.
    * 
    * @param attributeType
    * @param value
    */
   public AttributeValueCriteria(DynamicAttributeDescriptor attributeType, String value) {
      super();
      this.attributeType = attributeType;
      this.value = value;
   }

   /**
    * Constructor for search criteria that finds an attribute of the given type with its current value equal to the
    * given value.
    * 
    * @param attributeType
    * @param value
    * @throws SQLException
    */
   public AttributeValueCriteria(String attributeTypeName, String value) throws SQLException {
      this(ConfigurationPersistenceManager.getInstance().getDynamicAttributeType(attributeTypeName), value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria#addToTableSql(org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQueryBuilder)
    */
   @Override
   public void addToTableSql(ArtifactQueryBuilder builder) {
      attrAlias = builder.appendAliasedTable("osee_define_attribute");
      txsAlias = builder.appendAliasedTable("osee_define_txs");
      txdAlias = builder.appendAliasedTable("osee_define_tx_details");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria#addToWhereSql(org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQueryBuilder)
    */
   @Override
   public void addToWhereSql(ArtifactQueryBuilder builder) {
      if (attributeType != null) {
         builder.append(attrAlias);
         builder.append(".attr_type_id=? AND ");
         builder.addParameter(SQL3DataType.INTEGER, attributeType.getAttrTypeId());
      }
      if (value != null) {
         builder.append(attrAlias);
         builder.append(".value=? AND ");
         builder.addParameter(SQL3DataType.VARCHAR, value);
      }
      builder.append(attrAlias);
      builder.append(".gamma_id=");
      builder.append(txsAlias);
      builder.append(".gamma_id AND ");

      builder.addCurrentTxSql(txsAlias, txdAlias);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria#addJoinArtId(org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQueryBuilder)
    */
   @Override
   public void addJoinArtId(ArtifactQueryBuilder builder) {
      builder.append(attrAlias);
      builder.append(".art_id");
   }
}