/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile.jira;

/**
 * @author Donald G. Dunne
 */
public class CreateStoryResponse {

   String id;
   String key;
   String self;

   public CreateStoryResponse() {
      // for JaxRs
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getSelf() {
      return self;
   }

   public void setSelf(String self) {
      this.self = self;
   }

}
