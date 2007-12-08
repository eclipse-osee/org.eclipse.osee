/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.plugin.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Roberto E. Escobar
 */
public class UserCredentials {

   public enum UserCredentialEnum implements Serializable {
      Name, Email, Id, Domain, UserName, Password;
   }

   private Map<UserCredentialEnum, Pair<Boolean, String>> credentialsMap;

   public UserCredentials() {
      super();
      credentialsMap = new HashMap<UserCredentialEnum, Pair<Boolean, String>>();
   }

   public boolean isValid(UserCredentialEnum field) {
      Pair<Boolean, String> pair = credentialsMap.get(field);
      if (pair != null) {
         return pair.getKey().booleanValue();
      }
      return false;
   }

   public String getField(UserCredentialEnum field) {
      Pair<Boolean, String> pair = credentialsMap.get(field);
      if (pair != null) {
         return pair.getValue();
      }
      return "";
   }

   public void setFieldAndValidity(UserCredentialEnum field, boolean isValid, String value) {
      Pair<Boolean, String> pair = credentialsMap.get(field);
      if (pair != null) {
         pair.setKey(new Boolean(isValid));
         pair.setValue(value);
      } else {
         credentialsMap.put(field, new Pair<Boolean, String>(new Boolean(isValid), value));
      }
   }

   public void setValid(UserCredentialEnum field, boolean isValid) {
      Pair<Boolean, String> pair = credentialsMap.get(field);
      if (pair != null) {
         pair.setKey(new Boolean(isValid));
      } else {
         credentialsMap.put(field, new Pair<Boolean, String>(new Boolean(isValid), ""));
      }
   }

   public void setField(UserCredentialEnum field, String value) {
      Pair<Boolean, String> pair = credentialsMap.get(field);
      if (pair != null) {
         pair.setValue(value);
      } else {
         credentialsMap.put(field, new Pair<Boolean, String>(new Boolean(false), value));
      }
   }

   protected static UserCredentials toCredentials(Map<String, String> info) {
      UserCredentials toReturn = new UserCredentials();
      for (UserCredentialEnum credentialEnum : UserCredentialEnum.values()) {
         String value = (String) info.get(credentialEnum.toString());
         if (value != null) {
            toReturn.setFieldAndValidity(credentialEnum, true, value);
         }
      }
      return toReturn;
   }

   public Map<String, String> toMap() {
      Map<String, String> infoMap = new HashMap<String, String>();
      Set<UserCredentialEnum> keys = credentialsMap.keySet();
      for (UserCredentialEnum credentialEnum : keys) {
         String key = credentialEnum.toString();
         String value = getField(credentialEnum);
         infoMap.put(key, value);
      }
      return infoMap;
   }

   public void clear() {
      credentialsMap.clear();
   }

   public String toString() {
      return String.format("%s", credentialsMap.toString());
   }
}
