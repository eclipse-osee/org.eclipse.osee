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
import static org.eclipse.osee.orcs.account.admin.internal.oauth.OAuthTypeTokenProvider.oauth;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Roberto E. Escobar
 */
public interface OAuthOseeTypes {

   // @formatter:off
   public static final ArtifactTypeToken OAuthClient = ArtifactTypeToken.valueOf(756912961500447526L, "OAuth Client");

   AttributeTypeString OAuthClientAuthorizedAudience = oauth.createString(7160371155049131554L, "oauth.client.Authorized Audience", MediaType.TEXT_PLAIN, "");
   AttributeTypeEnum OAuthClientAuthorizedGrantType = oauth.createEnum(1935002343589638144L, "oauth.client.Authorized Grant Type", MediaType.TEXT_PLAIN, "");
   AttributeTypeString OAuthClientAuthorizedRedirectUri = oauth.createString(5424134645937614632L, "oauth.client.Authorized Redirect URI", MediaType.TEXT_PLAIN, "");
   AttributeTypeString OAuthClientAuthorizedScope = oauth.createString(3555983643778551674L, "oauth.client.Authorized Scope", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean OAuthClientIsConfidential = oauth.createBoolean(537327028164749105L, "oauth.client.Is Confidential", MediaType.TEXT_PLAIN, "");
   AttributeTypeString OAuthClientLogoUri = oauth.createString(7843963586445815729L, "oauth.client.Logo URI", MediaType.TEXT_PLAIN, "");
   AttributeTypeString OAuthClientProperties = oauth.createString(5633616462036881674L, "oauth.client.Properties", MediaType.APPLICATION_JSON, "");
   AttributeTypeString OAuthClientWebsiteUri = oauth.createString(7824657901879283800L, "oauth.client.Website URI", MediaType.TEXT_PLAIN, "");

   public static final ArtifactToken OAUTH_TYPES = ArtifactToken.valueOf(7067755, "OAuthTypes", COMMON, CoreArtifactTypes.OseeTypeDefinition);
   // @formatter:on

}