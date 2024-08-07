/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.enums.token;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.OAuthTypeTokenProvider;
import org.eclipse.osee.framework.core.enums.token.OAuthClientGrantAttributeType.OAuthClientGrantEnum;

/**
 * @author Stephen J. Molaro
 */
public class OAuthClientGrantAttributeType extends AttributeTypeEnum<OAuthClientGrantEnum> {

   public final OAuthClientGrantEnum AuthorizationCodeGrant = new OAuthClientGrantEnum(0, "Authorization Code Grant");
   public final OAuthClientGrantEnum ImplicitGrant = new OAuthClientGrantEnum(1, "Implicit Grant");
   public final OAuthClientGrantEnum ResourceOwnerPasswordCredentialsGrant =
      new OAuthClientGrantEnum(2, "Resource Owner Password Credentials Grant");
   public final OAuthClientGrantEnum ClientCredentialsGrant = new OAuthClientGrantEnum(3, "Client Credentials Grant");

   public OAuthClientGrantAttributeType(NamespaceToken namespace, int enumCount) {
      super(1935002343589638144L, namespace, "oauth.client.Authorized Grant Type", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public OAuthClientGrantAttributeType() {
      this(OAuthTypeTokenProvider.OAUTH, 4);
   }

   public class OAuthClientGrantEnum extends EnumToken {
      public OAuthClientGrantEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}