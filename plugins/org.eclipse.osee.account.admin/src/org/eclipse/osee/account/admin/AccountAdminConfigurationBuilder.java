/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.admin;

import static org.eclipse.osee.account.admin.AccountConstants.ACCOUNT_DISPLAY_NAME_VALIDATION_PATTERN;
import static org.eclipse.osee.account.admin.AccountConstants.ACCOUNT_EMAIL_VALIDATION_PATTERN;
import static org.eclipse.osee.account.admin.AccountConstants.ACCOUNT_USERNAME_VALIDATION_PATTERN;
import static org.eclipse.osee.account.admin.AccountConstants.DEFAULT_DISPLAY_NAME_VALIDATION_PATTERN;
import static org.eclipse.osee.account.admin.AccountConstants.DEFAULT_EMAIL_VALIDATION_PATTERN;
import static org.eclipse.osee.account.admin.AccountConstants.DEFAULT_USERNAME_VALIDATION_PATTERN;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class AccountAdminConfigurationBuilder {

   private final AccountConfigurationImpl config = new AccountConfigurationImpl();

   private AccountAdminConfigurationBuilder() {
      //
   }

   public static AccountAdminConfigurationBuilder newBuilder() {
      return new AccountAdminConfigurationBuilder();
   }

   public AccountAdminConfigurationBuilder userNamePattern(String pattern) {
      config.setUserNamePattern(pattern);
      return this;
   }

   public AccountAdminConfigurationBuilder emailPattern(String pattern) {
      config.setEmailPattern(pattern);
      return this;
   }

   public AccountAdminConfigurationBuilder displayNamePattern(String pattern) {
      config.setDisplayNamePattern(pattern);
      return this;
   }

   public AccountAdminConfigurationBuilder properties(Map<String, Object> props) {
      config.loadProperties(props);
      return this;
   }

   public AccountAdminConfiguration build() {
      return config.clone();
   }

   private static final class AccountConfigurationImpl implements AccountAdminConfiguration, Cloneable {

      private String userNamePattern;
      private String emailPattern;
      private String displayPattern;

      @Override
      public String getUserNamePattern() {
         return userNamePattern;
      }

      @Override
      public String getEmailPattern() {
         return emailPattern;
      }

      @Override
      public String getDisplayNamePattern() {
         return displayPattern;
      }

      public void setUserNamePattern(String pattern) {
         userNamePattern = pattern;
      }

      public void setEmailPattern(String pattern) {
         emailPattern = pattern;
      }

      public void setDisplayNamePattern(String pattern) {
         displayPattern = pattern;
      }

      @Override
      public synchronized AccountAdminConfiguration clone() {
         AccountConfigurationImpl cloned = new AccountConfigurationImpl();
         cloned.userNamePattern = this.userNamePattern;
         cloned.emailPattern = this.emailPattern;
         cloned.displayPattern = this.displayPattern;
         return cloned;
      }

      public void loadProperties(Map<String, Object> props) {
         if (props != null && !props.isEmpty()) {
            setUserNamePattern(get(props, ACCOUNT_USERNAME_VALIDATION_PATTERN, DEFAULT_USERNAME_VALIDATION_PATTERN));
            setEmailPattern(get(props, ACCOUNT_EMAIL_VALIDATION_PATTERN, DEFAULT_EMAIL_VALIDATION_PATTERN));
            setDisplayNamePattern(get(props, ACCOUNT_DISPLAY_NAME_VALIDATION_PATTERN,
               DEFAULT_DISPLAY_NAME_VALIDATION_PATTERN));
         }
      }

      private String get(Map<String, Object> props, String key, String defaultValue) {
         String toReturn = defaultValue;
         Object object = props.get(key);
         if (object != null) {
            toReturn = String.valueOf(object);
         }
         return toReturn;
      }

      @Override
      public String toString() {
         return "AccountConfigurationImpl [userNamePattern=" + userNamePattern + ", emailPattern=" + emailPattern + ", displayPattern=" + displayPattern + "]";
      }

   }

}
