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

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.io.ByteSource;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.security.OAuthClient;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class ClientArtifact extends BaseIdentity<String> implements OAuthClient {

   private final ArtifactReadable artifact;
   private final OAuthClientCredential credential;
   private ByteSource logoSupplier;

   public ClientArtifact(ArtifactReadable artifact, OAuthClientCredential credential) {
      super(artifact.getGuid());
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
      return artifact.getSoleAttributeAsString(OAuthOseeTypes.OAuthClientWebsiteUri);
   }

   @Override
   public String getApplicationLogoUri() {
      return artifact.getSoleAttributeAsString(OAuthOseeTypes.OAuthClientLogoUri);
   }

   @Override
   public boolean isConfidential() {
      return artifact.getSoleAttributeValue(OAuthOseeTypes.OAuthClientIsConfidential);
   }

   @Override
   public List<String> getAllowedGrantTypes() {
      return artifact.getAttributeValues(OAuthOseeTypes.OAuthClientAuthorizedGrantType);
   }

   @Override
   public List<String> getRedirectUris() {
      return artifact.getAttributeValues(OAuthOseeTypes.OAuthClientAuthorizedRedirectUri);
   }

   @Override
   public List<String> getRegisteredScopes() {
      return artifact.getAttributeValues(OAuthOseeTypes.OAuthClientAuthorizedScope);
   }

   @Override
   public List<String> getRegisteredAudiences() {
      return artifact.getAttributeValues(OAuthOseeTypes.OAuthClientAuthorizedAudience);
   }

   @Override
   public Map<String, String> getProperties() {
      String data = artifact.getSoleAttributeValue(OAuthOseeTypes.OAuthClientProperties, null);
      if (Strings.isValid(data)) {
         return JsonUtil.readValue(data, new TypeReference<Map<String, String>>() {// used to avoid type erasure
         });
      }
      return Collections.emptyMap();
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
   public ByteSource getApplicationLogoSupplier() {
      return logoSupplier;
   }

   public void setApplicationLogoSupplier(ByteSource supplier) {
      this.logoSupplier = supplier;
   }

   public InputStream getInput() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.ImageContent);
   }

}
