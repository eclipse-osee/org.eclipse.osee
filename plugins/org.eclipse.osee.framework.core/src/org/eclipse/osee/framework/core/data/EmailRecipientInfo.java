/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.framework.core.data;

public class EmailRecipientInfo {
   private String email;
   private String publicCertificate;

   public EmailRecipientInfo() {
      // for JSON serialization
   }

   public EmailRecipientInfo(String email, String publicCertificate) {
      this.email = email;
      this.publicCertificate = publicCertificate;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getPublicCertificate() {
      return publicCertificate;
   }

   public void setPublicCertificate(String publicCertificate) {
      this.publicCertificate = publicCertificate;
   }
}
