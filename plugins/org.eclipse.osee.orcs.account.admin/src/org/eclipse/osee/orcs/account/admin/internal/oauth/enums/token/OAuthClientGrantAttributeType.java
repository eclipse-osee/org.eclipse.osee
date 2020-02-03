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
package org.eclipse.osee.orcs.account.admin.internal.oauth.enums.token;

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.orcs.account.admin.internal.oauth.enums.token.OAuthClientGrantAttributeType.OAuthClientGrantEnum;

/**
 * @author Stephen J. Molaro
 */
public class OAuthClientGrantAttributeType extends AttributeTypeEnum<OAuthClientGrantEnum> {

   public final OAuthClientGrantEnum AuthorizationCodeGrant = new OAuthClientGrantEnum(0, "Authorization Code Grant");
   public final OAuthClientGrantEnum ImplicitGrant = new OAuthClientGrantEnum(1, "Implicit Grant");
   public final OAuthClientGrantEnum ResourceOwnerPasswordCredentialsGrant =
      new OAuthClientGrantEnum(2, "Resource Owner Password Credentials Grant");
   public final OAuthClientGrantEnum ClientCredentialsGrant = new OAuthClientGrantEnum(3, "Client Credentials Grant");

   public OAuthClientGrantAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1935002343589638144L, namespace, "oauth.client.Authorized Grant Type", mediaType, "", taggerType, 4);
   }

   public class OAuthClientGrantEnum extends EnumToken {
      public OAuthClientGrantEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}