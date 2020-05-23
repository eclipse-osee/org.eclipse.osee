/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.jaxrs.server.session;

import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;

/**
 * @author Angel Avila
 */
public class SessionData extends BaseIdentity<String> {

   private UserSubject subject;
   private Long issuedAt;
   private Long expiresIn;
   private String subjectToken;
   private String accountName;
   private String accountUsername;
   private Long accountId;
   private String accountDisplayName;
   private String accountEmail;
   private boolean accountActive;

   public SessionData(String id) {
      super(id);
   }

   public UserSubject getSubject() {
      return subject;
   }

   public Long getIssuedAt() {
      return issuedAt;
   }

   public Long getExpiresIn() {
      return expiresIn;
   }

   public String getSubjectToken() {
      return subjectToken;
   }

   public String getAccountName() {
      return accountName;
   }

   public String getAccountUsername() {
      return accountUsername;
   }

   public Long getAccountId() {
      return accountId;
   }

   public String getAccountDisplayName() {
      return accountDisplayName;
   }

   public String getAccountEmail() {
      return accountEmail;
   }

   public boolean getAccountActive() {
      return accountActive;
   }

   public void setSubject(UserSubject subject) {
      this.subject = subject;
   }

   public void setIssuedAt(Long issuedAt) {
      this.issuedAt = issuedAt;
   }

   public void setExpiresIn(Long expiresIn) {
      this.expiresIn = expiresIn;
   }

   public void setSubjectToken(String subjectToken) {
      this.subjectToken = subjectToken;
   }

   public void setAccountName(String accountName) {
      this.accountName = accountName;
   }

   public void setAccountUsername(String accountUsername) {
      this.accountUsername = accountUsername;
   }

   public void setAccountId(Long accountId) {
      this.accountId = accountId;
   }

   public void setAccountDisplayName(String accountDisplayName) {
      this.accountDisplayName = accountDisplayName;
   }

   public void setAccountEmail(String accountEmail) {
      this.accountEmail = accountEmail;
   }

   public void setAccountActive(boolean accountActive) {
      this.accountActive = accountActive;
   }

}
