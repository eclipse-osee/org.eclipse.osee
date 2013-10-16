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
package org.eclipse.osee.orcs.db.internal.util;

import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.core.util.HexUtil;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeSequence;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.db.internal.IdentityManager;

/**
 * @author Roberto E. Escobar
 */
public class IdentityManagerImpl implements IdentityManager {

   private static final String SELECT_BRANCH_TOKEN_BY_ID =
      "select branch_guid, branch_name from osee_branch where branch_id = ?";

   private static final String SELECT_BRANCH_ID_BY_GUID = "select branch_id from osee_branch where branch_guid = ?";

   private final IOseeDatabaseService dbService;
   private final IdentityService idService;

   public IdentityManagerImpl(IOseeDatabaseService dbService, IdentityService idService) {
      super();
      this.dbService = dbService;
      this.idService = idService;
   }

   private IOseeSequence getSequence() throws OseeDataStoreException {
      return dbService.getSequence();
   }

   @Override
   public int getNextArtifactId() throws OseeCoreException {
      return getSequence().getNextArtifactId();
   }

   @Override
   public int getNextAttributeId() throws OseeCoreException {
      return getSequence().getNextAttributeId();
   }

   @Override
   public int getNextRelationId() throws OseeCoreException {
      return getSequence().getNextRelationId();
   }

   @Override
   public long getNextGammaId() throws OseeCoreException {
      return getSequence().getNextGammaId();
   }

   @Override
   public String getUniqueGuid(String guid) {
      String toReturn = guid;
      if (toReturn == null) {
         toReturn = GUID.create();
      }
      return toReturn;
   }

   @Override
   public int parseToLocalId(String value) throws OseeCoreException {
      Long uuid = HexUtil.toLong(value);
      return getLocalId(uuid);
   }

   @Override
   public Integer getLocalId(Long universalId) throws OseeCoreException {
      return idService.getLocalId(universalId);
   }

   @Override
   public Long getUniversalId(Integer localId) throws OseeCoreException {
      return idService.getUniversalId(localId);
   }

   @Override
   public int getLocalId(Identity<Long> identity) throws OseeCoreException {
      return idService.getLocalId(identity);
   }

   @Override
   public void store(List<Long> toPersist) throws OseeCoreException {
      idService.store(toPersist);
   }

   @Override
   public void invalidateIds() throws OseeDataStoreException {
      getSequence().clear();
      idService.clear();
   }

   @Override
   public int getLocalId(IOseeBranch branch) throws OseeCoreException {
      int toReturn = dbService.runPreparedQueryFetchObject(-1, SELECT_BRANCH_ID_BY_GUID, branch.getGuid());
      Conditions.checkExpressionFailOnTrue(toReturn < 0, "Error getting branch_id for branch: [%s]", branch);
      return toReturn;
   }

   @Override
   public IOseeBranch getBranch(int branchId) throws OseeCoreException {
      IOseeBranch toReturn = null;
      IOseeStatement stmt = dbService.getStatement();
      try {
         stmt.runPreparedQuery(SELECT_BRANCH_TOKEN_BY_ID, branchId);
         while (stmt.next()) {
            String guid = stmt.getString("branch_guid");
            String name = stmt.getString("branch_name");
            toReturn = TokenFactory.createBranch(guid, name);
         }
      } finally {
         stmt.close();
      }
      return toReturn;
   }

}
