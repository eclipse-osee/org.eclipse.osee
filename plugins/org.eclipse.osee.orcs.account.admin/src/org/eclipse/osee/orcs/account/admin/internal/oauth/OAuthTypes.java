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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
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
   public static final IArtifactType OAUTH_CLIENT = TokenFactory.createArtifactType(0x0A811854041F8326L, "OAuth Client");

   public static final IAttributeType OAUTH_CLIENT_WEBSITE_URI = TokenFactory.createAttributeType(0x6C96C4E7AA77D058L, "Website URL");
   public static final IAttributeType OAUTH_CLIENT_LOGO_URI = TokenFactory.createAttributeType(0x6CDB5B52A133AFB1L, "Logo URL");

   public static final IAttributeType OAUTH_CLIENT_IS_CONFIDENTIAL = TokenFactory.createAttributeType(0x0774F815D1F9E731L, "Is Confidential");
   public static final IAttributeType OAUTH_CLIENT_PROPERTIES = TokenFactory.createAttributeType(0x4E2EA052F480510AL, "Properties");

   public static final IAttributeType OAUTH_CLIENT_AUTHORIZED_GRANT_TYPE = TokenFactory.createAttributeType(0x1ADA826121357000L, "Authorized Grant Type");
   public static final IAttributeType OAUTH_CLIENT_AUTHORIZED_REDIRECT_URI = TokenFactory.createAttributeType(0x4B4665B8E1002F28L, "Authorized Redirect URI");
   public static final IAttributeType OAUTH_CLIENT_AUTHORIZED_SCOPE = TokenFactory.createAttributeType(0x315964489A850F7AL, "Authorized Scope");
   public static final IAttributeType OAUTH_CLIENT_AUTHORIZED_AUDIENCE = TokenFactory.createAttributeType(0x635EBFA0D4A82E22L, "Authorized Audience");

   public static final ArtifactToken OAUTH_TYPES = ArtifactToken.valueOf(7067755, "OAuthTypes", COMMON, CoreArtifactTypes.OseeTypeDefinition);

   // @formatter:on

}
