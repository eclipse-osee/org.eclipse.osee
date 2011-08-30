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
package org.eclipse.osee.framework.core.server;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.server.internal.ServerActivator;
import org.eclipse.osee.framework.core.server.internal.ServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public final class UserDataStore {
   private static final String LOAD_OSEE_USER =
      "select att.value as user_id from osee_attribute att, osee_txs txs where att.attr_type_id = ? and txs.branch_id = ? and att.gamma_id = txs.gamma_id and txs.tx_current = ? and att.value = ?";

   private UserDataStore() {
      // private constructor
   }

   public static IUserToken getUserTokenFromOseeDb(String userId) {
      IUserToken toReturn = null;
      IOseeStatement chStmt = null;
      IOseeCachingService cachingService = ServiceProvider.getCachingService();
      try {

         int attributeTypeId = cachingService.getAttributeTypeCache().getLocalId(CoreAttributeTypes.UserId);
         Branch branch = cachingService.getBranchCache().get(CoreBranches.COMMON);
         if (branch != null) {
            int branchId = branch.getId();

            chStmt = ConnectionHandler.getStatement();
            chStmt.runPreparedQuery(LOAD_OSEE_USER, attributeTypeId, branchId, TxChange.CURRENT.getValue(), userId);
            if (chStmt.next()) {
               toReturn =
                  TokenFactory.createUserToken(GUID.create(), "-", "-", chStmt.getString("user_id"), true, false, false);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.logf(ServerActivator.class, Level.SEVERE, ex, "Unable to find userId [%s] in OSEE database.", userId);
      } finally {
         Lib.close(chStmt);
      }
      return toReturn;
   }

   public static IUserToken createUserToken(boolean isCreationRequired, String userName, String userId, String userEmail, boolean isActive) {
      return TokenFactory.createUserToken(GUID.create(), userName, userEmail, userId, isActive, false,
         isCreationRequired);
   }

}