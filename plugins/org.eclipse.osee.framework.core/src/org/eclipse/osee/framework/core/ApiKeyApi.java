/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ApiKey;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.KeyScopeContainer;

public interface ApiKeyApi {
   List<ApiKey> getApiKeys(ArtifactId userArtId);

   ApiKey getApiKey(String apiKeyString);

   Map<String, String> createApiKey(ApiKey apiKey, ArtifactId userArtId);

   public List<KeyScopeContainer> getKeyScopes();

   boolean revokeApiKey(long keyUID);

   boolean checkKeyExpiration(ApiKey apiKey);
}
