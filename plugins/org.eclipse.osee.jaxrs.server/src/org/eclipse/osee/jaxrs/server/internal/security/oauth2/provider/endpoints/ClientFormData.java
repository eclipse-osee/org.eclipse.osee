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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Roberto E. Escobar
 */
public class ClientFormData extends BaseIdentity<String> implements Named {

   private UserSubject userSubject;
   private String name;
   private String description;
   private String webUri;
   private String logoUri;
   private List<String> certificates = new LinkedList<>();

   private List<String> redirectUris;

   private InputStream logoContent;
   private Map<String, String> logoParameters;

   private boolean isConfidential;
   private List<String> allowedGrantTypes = new LinkedList<>();
   private List<String> allowedScopes = new LinkedList<>();
   private List<String> allowedAudiences = new LinkedList<>();

   private Map<String, String> properties = new HashMap<>();

   public ClientFormData(String id) {
      super(id);
   }

   public UserSubject getUserSubject() {
      return userSubject;
   }

   @Override
   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public String getWebUri() {
      return webUri;
   }

   public List<String> getRedirectUris() {
      return redirectUris;
   }

   public InputStream getLogoContent() {
      return logoContent;
   }

   public Map<String, String> getLogoParameters() {
      return logoParameters;
   }

   public void setUserSubject(UserSubject userSubject) {
      this.userSubject = userSubject;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setWebUri(String websiteUri) {
      this.webUri = websiteUri;
   }

   public void setRedirectUris(List<String> redirectUris) {
      this.redirectUris = redirectUris;
   }

   public void setLogoContent(InputStream logoContent) {
      this.logoContent = logoContent;
   }

   public void setLogoParameters(Map<String, String> logoParameters) {
      this.logoParameters = logoParameters;
   }

   public boolean isLogoAvailable() {
      return logoContent != null;
   }

   public List<String> getCertificates() {
      return certificates;
   }

   public boolean isConfidential() {
      return isConfidential;
   }

   public List<String> getAllowedGrantTypes() {
      return allowedGrantTypes;
   }

   public List<String> getAllowedScopes() {
      return allowedScopes;
   }

   public List<String> getAllowedAudiences() {
      return allowedAudiences;
   }

   public Map<String, String> getProperties() {
      return properties;
   }

   public void setCertificates(List<String> certificates) {
      this.certificates = certificates;
   }

   public void setConfidential(boolean isConfidential) {
      this.isConfidential = isConfidential;
   }

   public void setAllowedGrantTypes(List<String> allowedGrantTypes) {
      this.allowedGrantTypes = allowedGrantTypes;
   }

   public void setAllowedScopes(List<String> allowedScopes) {
      this.allowedScopes = allowedScopes;
   }

   public void setAllowedAudiences(List<String> allowedAudiences) {
      this.allowedAudiences = allowedAudiences;
   }

   public void setProperties(Map<String, String> properties) {
      this.properties = properties;
   }

   public String getLogoUri() {
      return logoUri;
   }

   public void setLogoUri(String logoUri) {
      this.logoUri = logoUri;
   }

}