/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters;

import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.eclipse.osee.jaxrs.server.security.OAuthToken;
import org.eclipse.osee.jaxrs.server.security.OAuthTokenType;

/**
 * @author Roberto E. Escobar
 */
public class RefreshOAuthToken extends org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken implements OAuthToken {

   private static final long serialVersionUID = 5893901939888969786L;

   private final long uuid;
   private final long clientId;
   private final long subjectId;
   private final OAuthTokenType type = OAuthTokenType.REFRESH_TOKEN;

   public RefreshOAuthToken(long uuid, long clientId, long subjectId) {
      super();
      this.uuid = uuid;
      this.clientId = clientId;
      this.subjectId = subjectId;
      setTokenType(OAuthConstants.REFRESH_TOKEN_TYPE);
   }

   @Override
   public long getUuid() {
      return uuid;
   }

   @Override
   public long getSubjectId() {
      return subjectId;
   }

   @Override
   public long getClientId() {
      return clientId;
   }

   @Override
   public OAuthTokenType getType() {
      return type;
   }

}
