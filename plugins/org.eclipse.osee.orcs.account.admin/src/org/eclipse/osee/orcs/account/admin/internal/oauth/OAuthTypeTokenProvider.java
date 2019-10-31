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
package org.eclipse.osee.orcs.account.admin.internal.oauth;

import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProviderBase;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;

/**
 * @author Ryan D. Brooks
 */
public final class OAuthTypeTokenProvider extends OrcsTypeTokenProviderBase {
   private static final NamespaceToken OAUTH =
      NamespaceToken.valueOf(6, "oauth", "Namespace for oauth system and content management types");

   public static final OrcsTypeTokens oauth = new OrcsTypeTokens(OAUTH);

   public OAuthTypeTokenProvider() {
      super(oauth);
   }
}