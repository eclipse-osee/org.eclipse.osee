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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.search.engine.utility.SearchTagQueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public final class AttributeDataStore {

   private static final String LOAD_ATTRIBUTE =
      "SELECT attr1.gamma_id, attr1.VALUE, attr1.uri, attr1.attr_type_id FROM osee_attribute attr1, osee_tag_gamma_queue tgq1 WHERE attr1.gamma_id = tgq1.gamma_id AND tgq1.query_id = ?";

   private static final SearchTagQueryBuilder searchTagQueryBuilder = new SearchTagQueryBuilder();

   private IOseeDatabaseService databaseService;
   private IdentityService identityService;

   public void setDatabaseService(IOseeDatabaseService databaseService) {
      this.databaseService = databaseService;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   public Collection<AttributeData> getAttribute(final OseeConnection connection, final int tagQueueQueryId) throws OseeCoreException {
      final Collection<AttributeData> attributeData = new ArrayList<AttributeData>();

      IOseeStatement chStmt = databaseService.getStatement(connection);
      try {
         chStmt.runPreparedQuery(LOAD_ATTRIBUTE, tagQueueQueryId);
         while (chStmt.next()) {
            attributeData.add(new AttributeData(chStmt.getLong("gamma_id"), chStmt.getString("value"),
               chStmt.getString("uri"), chStmt.getInt("attr_type_id")));
         }
      } finally {
         chStmt.close();
      }

      return attributeData;
   }

   public Set<AttributeData> getAttributesByTags(int branchId, DeletionFlag deletionFlag, final Collection<Long> tagData, final Collection<IAttributeType> attributeTypes) throws OseeCoreException {
      final Set<AttributeData> toReturn = new HashSet<AttributeData>();

      IdJoinQuery oseeIdJoin = null;
      IOseeStatement chStmt = databaseService.getStatement();
      boolean useAttrTypeJoin = attributeTypes.size() > 1;

      try {
         String sqlQuery =
            searchTagQueryBuilder.getQuery(tagData.size(), useAttrTypeJoin, branchId, deletionFlag.areDeletedAllowed());
         List<Object> params = new ArrayList<Object>();
         params.addAll(tagData);

         if (attributeTypes.size() == 1) {
            sqlQuery += " and attr1.attr_type_id = ?";
         } else if (useAttrTypeJoin) {
            oseeIdJoin = JoinUtility.createIdJoinQuery();
            for (IAttributeType attributeType : attributeTypes) {
               oseeIdJoin.add(identityService.getLocalId(attributeType));
            }
            oseeIdJoin.store();
            params.add(oseeIdJoin.getQueryId());
         }

         if (branchId > -1) {
            params.add(branchId);
         }
         if (attributeTypes.size() == 1) {
            IAttributeType type = attributeTypes.iterator().next();
            params.add(identityService.getLocalId(type));
         }

         chStmt.runPreparedQuery(sqlQuery, params.toArray(new Object[params.size()]));
         while (chStmt.next()) {
            toReturn.add(new AttributeData(chStmt.getInt("art_id"), chStmt.getLong("gamma_id"),
               chStmt.getInt("branch_id"), chStmt.getString("value"), chStmt.getString("uri"),
               chStmt.getInt("attr_type_id")));
         }
      } finally {
         chStmt.close();
         if (useAttrTypeJoin) {
            oseeIdJoin.delete();
         }
      }
      return toReturn;
   }
}