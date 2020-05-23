/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.account.admin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public final class CreateAccountRequestBuilder {

   private boolean active;
   private String userName;
   private String email;
   private String displayName;
   private final Map<String, String> preferences = new HashMap<>();

   private CreateAccountRequestBuilder() {
      //
   }

   public static CreateAccountRequestBuilder newBuilder() {
      return new CreateAccountRequestBuilder();
   }

   public CreateAccountRequestBuilder active(boolean active) {
      this.active = active;
      return this;
   }

   public CreateAccountRequestBuilder userName(String userName) {
      this.userName = userName;
      return this;
   }

   public CreateAccountRequestBuilder email(String email) {
      this.email = email;
      return this;
   }

   public CreateAccountRequestBuilder displayName(String displayName) {
      this.displayName = displayName;
      return this;
   }

   public CreateAccountRequestBuilder prefs(Map<String, String> other) {
      if (other != null && !other.isEmpty()) {
         preferences.putAll(other);
      }
      return this;
   }

   public CreateAccountRequestBuilder pref(String key, String value) {
      preferences.put(key, value);
      return this;
   }

   public CreateAccountRequest build() {
      Map<String, String> prefs = new HashMap<>(preferences);
      return new CreateAccountRequestImpl(active, userName, email, displayName, prefs);
   }

   public static final class CreateAccountRequestImpl implements CreateAccountRequest {

      private final boolean active;
      private final String userName;
      private final String email;
      private final String displayName;
      private final Map<String, String> prefs;

      public CreateAccountRequestImpl(boolean active, String userName, String email, String displayName, Map<String, String> prefs) {
         super();
         this.active = active;
         this.userName = userName;
         this.email = email;
         this.displayName = displayName;
         this.prefs = prefs;
      }

      @Override
      public String getUserName() {
         return userName;
      }

      @Override
      public String getDisplayName() {
         return displayName;
      }

      @Override
      public String getEmail() {
         return email;
      }

      @Override
      public boolean isActive() {
         return active;
      }

      @Override
      public Map<String, String> getPreferences() {
         return prefs;
      }

      @Override
      public String toString() {
         return "CreateAccountRequest [displayName=" + displayName + ", userName=" + userName + ", email=" + email + ", active=" + active + ", prefs=" + prefs + "]";
      }

   };
}
