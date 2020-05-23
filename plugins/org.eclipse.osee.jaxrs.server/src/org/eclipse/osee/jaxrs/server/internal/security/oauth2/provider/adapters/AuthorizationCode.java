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

import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.eclipse.osee.jaxrs.server.security.OAuthCodeGrant;

/**
 * @author Roberto E. Escobar
 */
public class AuthorizationCode extends ServerAuthorizationCodeGrant implements OAuthCodeGrant {

   private static final long serialVersionUID = 6207464542209610574L;

   private final long uuid;
   private final long clientId;
   private final long subjectId;

   public AuthorizationCode(long uuid, long clientId, long subjectId) {
      super();
      this.uuid = uuid;
      this.clientId = clientId;
      this.subjectId = subjectId;
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

}
