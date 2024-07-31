/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.api.notify;

/**
 * @author Donald G. Dunne
 */
public class TestEmail {

   String email;
   String subject;

   public TestEmail() {
      // for jax-rs
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getSubject() {
      return subject;
   }

   public void setSubject(String subject) {
      this.subject = subject;
   }

   public static TestEmail create(String email, String subject) {
      TestEmail tEmail = new TestEmail();
      tEmail.setEmail(email);
      tEmail.setSubject(subject);
      return tEmail;
   }

}
