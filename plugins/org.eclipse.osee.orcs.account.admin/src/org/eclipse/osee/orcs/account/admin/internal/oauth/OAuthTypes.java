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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Roberto E. Escobar
 */
public final class OAuthTypes {

   private OAuthTypes() {
      // Constants
   }

   // @formatter:off
   public static final NamespaceToken OAUTH = NamespaceToken.valueOf(10, "oauth", "Namespace for oauth system and content management types");

   public static final ArtifactTypeToken OAUTH_CLIENT = ArtifactTypeToken.valueOf(756912961500447526L, "OAuth Client");

   public static final AttributeTypeToken OAuthClientWebsiteUri = AttributeTypeToken.valueOf(7824657901879283800L, "oauth.client.Website URI");
   public static final AttributeTypeToken OAuthClientLogoUri = AttributeTypeToken.valueOf(7843963586445815729L, "oauth.client.Logo URI");

   public static final AttributeTypeToken OAuthClientIsConfidential = AttributeTypeToken.valueOf(537327028164749105L, "oauth.client.Is Confidential");
   public static final AttributeTypeToken OAuthClientProperties = AttributeTypeToken.valueOf(5633616462036881674L, "oauth.client.Properties");

   public static final AttributeTypeToken OAuthClientAuthorizedGrantType = AttributeTypeToken.valueOf(1935002343589638144L, "oauth.client.Authorized Grant Type");
   public static final AttributeTypeToken OAuthClientAuthorizedRedirectUri = AttributeTypeToken.valueOf(5424134645937614632L, "oauth.client.Authorized Redirect URI");
   public static final AttributeTypeToken OAuthClientAuthorizedScope = AttributeTypeToken.valueOf(3555983643778551674L, "oauth.client.Authorized Scope");
   public static final AttributeTypeToken OAuthClientAuthorizedAudience = AttributeTypeToken.valueOf(7160371155049131554L, "oauth.client.Authorized Audience");

   public static final ArtifactToken OAUTH_TYPES = ArtifactToken.valueOf(7067755, "OAuthTypes", COMMON, CoreArtifactTypes.OseeTypeDefinition);

   // @formatter:on

}