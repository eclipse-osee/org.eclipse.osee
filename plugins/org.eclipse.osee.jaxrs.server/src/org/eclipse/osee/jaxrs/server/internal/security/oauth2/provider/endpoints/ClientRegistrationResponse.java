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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class ClientRegistrationResponse {

   private String resourceServerName;
   private String clientId;
   private String clientSecret;

   public String getResourceServerName() {
      return resourceServerName;
   }

   public String getClientId() {
      return clientId;
   }

   public String getClientSecret() {
      return clientSecret;
   }

   public void setResourceServerName(String resourceServerName) {
      this.resourceServerName = resourceServerName;
   }

   public void setClientId(String clientId) {
      this.clientId = clientId;
   }

   public void setClientSecret(String clientSecret) {
      this.clientSecret = clientSecret;
   }

}