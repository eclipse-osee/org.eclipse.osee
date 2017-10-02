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

import com.google.common.io.InputSupplier;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.security.OAuthClient;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class ClientArtifact extends BaseIdentity<String> implements OAuthClient, InputSupplier<InputStream> {

   private final GsonBuilder builder;
   private final ArtifactReadable artifact;
   private final OAuthClientCredential credential;

   public ClientArtifact(GsonBuilder builder, ArtifactReadable artifact, OAuthClientCredential credential) {
      super(artifact.getGuid());
      this.builder = builder;
      this.credential = credential;
      this.artifact = artifact;
   }

   @Override
   public long getClientUuid() {
      return credential.getClientId();
   }

   @Override
   public long getSubjectId() {
      return credential.getSubjectId();
   }

   @Override
   public String getApplicationName() {
      return artifact.getName();
   }

   @Override
   public String getApplicationDescription() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.Description);
   }

   @Override
   public String getApplicationWebUri() {
      return artifact.getSoleAttributeAsString(OAuthTypes.OAUTH_CLIENT_WEBSITE_URI);
   }

   @Override
   public String getApplicationLogoUri() {
      return artifact.getSoleAttributeAsString(OAuthTypes.OAUTH_CLIENT_LOGO_URI);
   }

   @Override
   public boolean isConfidential() {
      return artifact.getSoleAttributeValue(OAuthTypes.OAUTH_CLIENT_IS_CONFIDENTIAL);
   }

   @Override
   public List<String> getAllowedGrantTypes() {
      return artifact.getAttributeValues(OAuthTypes.OAUTH_CLIENT_AUTHORIZED_GRANT_TYPE);
   }

   @Override
   public List<String> getRedirectUris() {
      return artifact.getAttributeValues(OAuthTypes.OAUTH_CLIENT_AUTHORIZED_REDIRECT_URI);
   }

   @Override
   public List<String> getRegisteredScopes() {
      return artifact.getAttributeValues(OAuthTypes.OAUTH_CLIENT_AUTHORIZED_SCOPE);
   }

   @Override
   public List<String> getRegisteredAudiences() {
      return artifact.getAttributeValues(OAuthTypes.OAUTH_CLIENT_AUTHORIZED_AUDIENCE);
   }

   @Override
   public Map<String, String> getProperties() {
      Map<String, String> toReturn = Collections.emptyMap();
      String data = artifact.getSoleAttributeValue(OAuthTypes.OAUTH_CLIENT_PROPERTIES, null);
      if (Strings.isValid(data)) {
         Gson gson = builder.create();
         Type typeOfHashMap = new TypeToken<Map<String, String>>() { //
         }.getType();
         toReturn = gson.fromJson(data, typeOfHashMap);
      }
      return toReturn;
   }

   @Override
   public String getClientId() {
      return credential.getClientKey();
   }

   @Override
   public String getClientSecret() {
      return credential.getClientSecret();
   }

   @Override
   public List<String> getApplicationCertificates() {
      return credential.getClientCertificates();
   }

   @Override
   public boolean hasApplicationLogoSupplier() {
      return artifact.getAttributeCount(CoreAttributeTypes.ImageContent) > 0;
   }

   @Override
   public InputSupplier<InputStream> getApplicationLogoSupplier() {
      return this;
   }

   @Override
   public InputStream getInput() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.ImageContent);
   }

}
