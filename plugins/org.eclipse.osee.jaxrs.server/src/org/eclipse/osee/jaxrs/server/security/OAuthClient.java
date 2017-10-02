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
package org.eclipse.osee.jaxrs.server.security;

import com.google.common.io.InputSupplier;
import java.io.InputStream;
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

   InputSupplier<InputStream> getApplicationLogoSupplier();

}
