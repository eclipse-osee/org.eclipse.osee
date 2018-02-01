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

import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractAuthenticationProvider implements IAuthenticationProvider {

   private Log logger;
   private OrcsApi orcsApi;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   protected Log getLogger() {
      return logger;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   protected OrcsApi getOrcsApi() {
      return orcsApi;
   }

   protected UserToken getUserTokenFromOseeDb(String userId) {
      UserToken toReturn = null;
      try {
         QueryFactory queryFactory = orcsApi.getQueryFactory();
         QueryBuilder query = queryFactory.fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.User).and(
            CoreAttributeTypes.UserId, userId);

         ArtifactReadable artifact = query.getResults().getOneOrNull();
         if (artifact != null) {
            toReturn = UserToken.create(artifact.getUuid(), artifact.getName(), artifact.getSoleAttributeAsString(CoreAttributeTypes.Email, ""),
               userId, true, false, false);
         } else {
            getLogger().info("Unable to find userId:[%s] on [%s]", userId, CoreBranches.COMMON);
         }
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Unable to find userId [%s] in OSEE database.", userId);
      }
      return toReturn;
   }

   protected UserToken createUserToken(boolean isCreationRequired, String userName, String userId, String userEmail, boolean isActive) {
      return UserToken.create(Lib.generateArtifactIdAsInt(), userName, userEmail, userId, isActive, false,
         isCreationRequired);
   }
}
