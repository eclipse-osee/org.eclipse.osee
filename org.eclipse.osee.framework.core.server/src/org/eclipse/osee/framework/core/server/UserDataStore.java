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
import org.eclipse.osee.framework.core.data.IOseeUserInfo;
import org.eclipse.osee.framework.core.data.OseeUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class UserDataStore {
   private static final String LOAD_OSEE_USER =
         "select oa.value as user_id from osee_attribute_type oat, osee_attribute oa, osee_txs txs where oat.name = 'User Id' and oat.attr_type_id = oa.attr_type_id and oa.gamma_id = txs.gamma_id and txs.tx_current = 1 and oa.value = ?";

   private UserDataStore() {
   }

   public static IOseeUserInfo getOseeUserFromOseeDb(String userId) {
      IOseeUserInfo toReturn = null;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(LOAD_OSEE_USER, userId);
         if (chStmt.next()) {
            // Only need the userId all other fields will be loaded by the client
            toReturn = new OseeUserInfo(false, "-", chStmt.getString("user_id"), "-", false);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(CoreServerActivator.class, Level.SEVERE, String.format(
               "Unable to find userId [%s] in OSEE database.", userId), ex);
      } finally {
         chStmt.close();
      }
      return toReturn;
   }

   public static IOseeUserInfo createUser(boolean isCreationRequired, String userName, String userId, String userEmail, boolean isActive) {
      return new OseeUserInfo(isCreationRequired, userName, userId, userEmail, isActive);
   }

   private final static class OseeUserInfo extends OseeUser implements IOseeUserInfo {
      private static final long serialVersionUID = 6770020451554391030L;
      private final boolean isCreationRequired;

      private OseeUserInfo(boolean isCreationRequired, String userName, String userId, String userEmail, boolean isActive) {
         super(userName, userId, userEmail, isActive);
         this.isCreationRequired = isCreationRequired;
      }

      @Override
      public boolean isCreationRequired() {
         return isCreationRequired;
      }
   }
}