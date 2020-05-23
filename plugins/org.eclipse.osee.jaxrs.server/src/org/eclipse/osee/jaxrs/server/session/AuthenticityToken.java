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

/**
 * @author Angel Avila
 */
public class AuthenticityToken {

   private Long subjectId;
   private String token;

   public AuthenticityToken() {
   }

   public Long getSubjectId() {
      return subjectId;
   }

   public String getToken() {
      return token;
   }

   public void setSubjectId(Long subjectId) {
      this.subjectId = subjectId;
   }

   public void setToken(String token) {
      this.token = token;
   }
}
