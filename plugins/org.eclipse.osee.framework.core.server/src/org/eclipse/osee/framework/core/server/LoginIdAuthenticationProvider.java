/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.server;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
public class LoginIdAuthenticationProvider extends AbstractAuthenticationProvider {

   // Direct query to quickly get token prior to framework coming up
   private static final String ART_ID_FROM_LOGIN_ID =
      "select attr_type_id, value from osee_attribute attr, osee_txs txs where txs.BRANCH_ID = 570 and txs.TX_CURRENT = 1 AND " + //
         "txs.gamma_id = attr.gamma_id and attr.art_id " + //
         "IN (select attr.art_id from osee_attribute attr, osee_txs txs where txs.BRANCH_ID = 570 and txs.TX_CURRENT = 1 AND " + //
         "txs.gamma_id = attr.gamma_id and attr.attr_type_id = 239475839435799 AND attr.value = ?) AND attr.attr_type_id " + //
         "IN (1152921504606847088, 1152921504606847073, 1152921504606847082)";

   private JdbcService jdbcService;

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   @Override
   public String getProtocol() {
      return "loginId";
   }

   @Override
   public boolean authenticate(OseeCredential credential) {
      return true;
   }

   @Override
   public UserToken asOseeUserId(OseeCredential credential) {
      String loginId = credential.getUserName();
      try {
         final Map<Long, String> typeIdToValue = new HashMap<Long, String>(3);
         jdbcService.getClient().runQuery(stmt -> {
            typeIdToValue.put(stmt.getLong("attr_type_id"), stmt.getString("value"));
         }, ART_ID_FROM_LOGIN_ID, loginId);
         if (typeIdToValue.isEmpty()) {
            throw new OseeArgumentException("Authentication: User with loginId [%s] does not exist.", loginId);
         }
         UserToken userTok = UserToken.create(typeIdToValue.get(CoreAttributeTypes.Name.getId()),
            typeIdToValue.get(CoreAttributeTypes.Email.getId()), typeIdToValue.get(CoreAttributeTypes.UserId.getId()));
         getLogger().info("Authentication: LoginId: [%s] UserToken:[%s]", loginId, userTok);
         return userTok;
      } catch (Exception ex) {
         getLogger().error(
            String.format("Exception resolving loginId: [%s] - Exception: %s", loginId, Lib.exceptionToString(ex)));
      }

      return SystemUser.UnAuthenticated;
   }

}
