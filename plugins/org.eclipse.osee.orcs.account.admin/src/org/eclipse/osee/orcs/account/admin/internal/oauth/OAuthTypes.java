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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Roberto E. Escobar
 */
public final class OAuthTypes {

   private OAuthTypes() {
      // Constants
   }

   // @formatter:off
   public static final ArtifactTypeToken OAUTH_CLIENT = TokenFactory.createArtifactType(756912961500447526L, "OAuth Client");

   public static final AttributeTypeToken OAUTH_CLIENT_WEBSITE_URI = AttributeTypeToken.valueOf(7824657901879283800L, "Website URL");
   public static final AttributeTypeToken OAUTH_CLIENT_LOGO_URI = AttributeTypeToken.valueOf(7843963586445815729L, "Logo URL");

   public static final AttributeTypeToken OAUTH_CLIENT_IS_CONFIDENTIAL = AttributeTypeToken.valueOf(537327028164749105L, "Is Confidential");
   public static final AttributeTypeToken OAUTH_CLIENT_PROPERTIES = AttributeTypeToken.valueOf(5633616462036881674L, "Properties");

   public static final AttributeTypeToken OAUTH_CLIENT_AUTHORIZED_GRANT_TYPE = AttributeTypeToken.valueOf(1935002343589638144L, "Authorized Grant Type");
   public static final AttributeTypeToken OAUTH_CLIENT_AUTHORIZED_REDIRECT_URI = AttributeTypeToken.valueOf(5424134645937614632L, "Authorized Redirect URI");
   public static final AttributeTypeToken OAUTH_CLIENT_AUTHORIZED_SCOPE = AttributeTypeToken.valueOf(3555983643778551674L, "Authorized Scope");
   public static final AttributeTypeToken OAUTH_CLIENT_AUTHORIZED_AUDIENCE = AttributeTypeToken.valueOf(7160371155049131554L, "Authorized Audience");

   public static final ArtifactToken OAUTH_TYPES = ArtifactToken.valueOf(7067755, "OAuthTypes", COMMON, CoreArtifactTypes.OseeTypeDefinition);

   // @formatter:on

}
