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
public class ClientRegistrationData {

   private String clientGuid;
   private String replyTo;
   private String authenticityToken;
   private String endUserName;

   public String getClientGuid() {
      return clientGuid;
   }

   public String getReplyTo() {
      return replyTo;
   }

   public String getAuthenticityToken() {
      return authenticityToken;
   }

   public String getEndUserName() {
      return endUserName;
   }

   public void setClientGuid(String clientGuid) {
      this.clientGuid = clientGuid;
   }

   public void setReplyTo(String replyTo) {
      this.replyTo = replyTo;
   }

   public void setAuthenticityToken(String authenticityToken) {
      this.authenticityToken = authenticityToken;
   }

   public void setEndUserName(String endUserName) {
      this.endUserName = endUserName;
   }

}