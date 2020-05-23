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

package org.eclipse.osee.jaxrs.server.security;

import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

/**
 * @author Roberto E. Escobar
 */
public enum OAuthTokenType {
   UNKNOW_TOKEN("unknown", -1),
   BEARER_TOKEN(OAuthConstants.BEARER_TOKEN_TYPE, 0),
   REFRESH_TOKEN(OAuthConstants.REFRESH_TOKEN_TYPE, 1),
   HAWK_TOKEN(OAuthConstants.HAWK_TOKEN_TYPE, 2);

   private final String tokenType;
   private final int value;

   private OAuthTokenType(String tokenType, int value) {
      this.tokenType = tokenType;
      this.value = value;
   }

   public int getValue() {
      return value;
   }

   public String getType() {
      return tokenType;
   }

   public static OAuthTokenType fromValue(int value) {
      OAuthTokenType toReturn = OAuthTokenType.UNKNOW_TOKEN;
      for (OAuthTokenType tokenType : OAuthTokenType.values()) {
         if (tokenType.getValue() == value) {
            toReturn = tokenType;
            break;
         }
      }
      return toReturn;
   }

   public static OAuthTokenType fromType(String tokenType) {
      OAuthTokenType toReturn = OAuthTokenType.UNKNOW_TOKEN;
      for (OAuthTokenType type : OAuthTokenType.values()) {
         if (type.getType().equals(tokenType)) {
            toReturn = type;
            break;
         }
      }
      return toReturn;
   }
}