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

import com.google.common.io.ByteSource;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.Identity;

/**
 * @author Roberto E. Escobar
 */
public interface OAuthClient extends Identity<String> {

   long getClientUuid();

   long getSubjectId();

   String getClientId();

   String getClientSecret();

   List<String> getApplicationCertificates();

   String getApplicationDescription();

   String getApplicationLogoUri();

   String getApplicationName();

   String getApplicationWebUri();

   Map<String, String> getProperties();

   List<String> getRedirectUris();

   List<String> getAllowedGrantTypes();

   List<String> getRegisteredAudiences();

   List<String> getRegisteredScopes();

   boolean isConfidential();

   boolean hasApplicationLogoSupplier();

   ByteSource getApplicationLogoSupplier();

}
