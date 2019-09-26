/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.account.admin.internal.oauth;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTokenService;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProvider;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Roberto E. Escobar
 */
public final class OAuthTypes implements OrcsTypeTokenProvider {
   private static final OrcsTypeTokens tokens = new OrcsTypeTokens();

   private OAuthTypes() {
      // Constants
   }

   // @formatter:off
   public static final NamespaceToken OAUTH = NamespaceToken.valueOf(10, "oauth", "Namespace for oauth system and content management types");

   public static final ArtifactTypeToken OAUTH_CLIENT = ArtifactTypeToken.valueOf(756912961500447526L, "OAuth Client");

   public static final AttributeTypeString OAuthClientAuthorizedAudience = tokens.add(AttributeTypeToken.createString(7160371155049131554L, OAUTH, "oauth.client.Authorized Audience", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum OAuthClientAuthorizedGrantType = tokens.add(AttributeTypeToken.createEnum(1935002343589638144L, OAUTH, "oauth.client.Authorized Grant Type", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString OAuthClientAuthorizedRedirectUri = tokens.add(AttributeTypeToken.createString(5424134645937614632L, OAUTH, "oauth.client.Authorized Redirect URI", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString OAuthClientAuthorizedScope = tokens.add(AttributeTypeToken.createString(3555983643778551674L, OAUTH, "oauth.client.Authorized Scope", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean OAuthClientIsConfidential = tokens.add(AttributeTypeToken.createBoolean(537327028164749105L, OAUTH, "oauth.client.Is Confidential", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString OAuthClientLogoUri = tokens.add(AttributeTypeToken.createString(7843963586445815729L, OAUTH, "oauth.client.Logo URI", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString OAuthClientProperties = tokens.add(AttributeTypeToken.createString(5633616462036881674L, OAUTH, "oauth.client.Properties", MediaType.APPLICATION_JSON, ""));
   public static final AttributeTypeString OAuthClientWebsiteUri = tokens.add(AttributeTypeToken.createString(7824657901879283800L, OAUTH, "oauth.client.Website URI", MediaType.TEXT_PLAIN, ""));

   public static final ArtifactToken OAUTH_TYPES = ArtifactToken.valueOf(7067755, "OAuthTypes", COMMON, CoreArtifactTypes.OseeTypeDefinition);

   // @formatter:on

   @Override
   public void registerTypes(OrcsTokenService tokenService) {
      tokens.registerTypes(tokenService);
   }

}