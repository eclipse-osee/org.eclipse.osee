/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.server;

import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractAuthenticationProvider implements IAuthenticationProvider {

   private static final String LOAD_OSEE_USER =
      "select att.value as user_id from osee_attribute att, osee_txs txs where att.attr_type_id = ? and txs.branch_id = ? and att.gamma_id = txs.gamma_id and txs.tx_current = ? and att.value = ?";

   private Log logger;
   private IOseeDatabaseService dbService;
   private IOseeCachingService cachingService;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   protected Log getLogger() {
      return logger;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   protected IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   public void setCachingService(IOseeCachingService cachingService) {
      this.cachingService = cachingService;
   }

   protected IOseeCachingService getCachingService() {
      return cachingService;
   }

   protected IUserToken getUserTokenFromOseeDb(String userId) {
      IUserToken toReturn = null;
      IOseeStatement chStmt = null;
      try {

         int attributeTypeId = getCachingService().getAttributeTypeCache().getLocalId(CoreAttributeTypes.UserId);
         Branch branch = getCachingService().getBranchCache().get(CoreBranches.COMMON);
         if (branch != null) {
            int branchId = branch.getId();

            chStmt = getDatabaseService().getStatement();
            chStmt.runPreparedQuery(LOAD_OSEE_USER, attributeTypeId, branchId, TxChange.CURRENT.getValue(), userId);
            if (chStmt.next()) {
               toReturn =
                  TokenFactory.createUserToken(GUID.create(), "-", "-", chStmt.getString("user_id"), true, false, false);
            }
         }
         if (toReturn == null) {
            getLogger().info("Unable to find userId:[%s] on [%s]", userId, branch);
         }
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Unable to find userId [%s] in OSEE database.", userId);
      } finally {
         Lib.close(chStmt);
      }
      return toReturn;
   }

   protected IUserToken createUserToken(boolean isCreationRequired, String userName, String userId, String userEmail, boolean isActive) {
      return TokenFactory.createUserToken(GUID.create(), userName, userEmail, userId, isActive, false,
         isCreationRequired);
   }
}
