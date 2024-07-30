/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ApiKey {
   private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   private String name;
   private List<KeyScopeContainer> scopes;
   private String creationDate;
   private String expirationDate;
   private String hashedValue;
   private String uniqueID;
   private final UserId userArtId;

   public ApiKey() {
      this.name = "";
      this.scopes = new ArrayList<KeyScopeContainer>();
      this.creationDate = "";
      this.expirationDate = "";
      this.hashedValue = "";
      this.uniqueID = "";
      this.userArtId = UserId.SENTINEL;
   }

   public ApiKey(String name, List<KeyScopeContainer> scopes, String creationDate, String expirationDate, String hashedValue, String uniqueID) {
      this.name = name;
      this.scopes = scopes;
      this.creationDate = creationDate;
      this.expirationDate = expirationDate;
      this.hashedValue = "";
      this.uniqueID = uniqueID != null ? uniqueID : "";
      this.userArtId = UserId.SENTINEL;
   }

   public ApiKey(String name, List<KeyScopeContainer> scopes, String creationDate, String expirationDate, String uniqueID, UserId userArtId) {
      this.name = name;
      this.scopes = scopes;
      this.creationDate = creationDate;
      this.expirationDate = expirationDate;
      this.hashedValue = "";
      this.uniqueID = uniqueID != null ? uniqueID : "";
      this.userArtId = userArtId;
   }

   public ApiKey(String name, List<KeyScopeContainer> scopes, String creationDate, String expirationDate, String uniqueID) {
      this.name = name;
      this.scopes = scopes;
      this.creationDate = creationDate;
      this.expirationDate = expirationDate;
      this.hashedValue = "";
      this.uniqueID = uniqueID != null ? uniqueID : "";
      this.userArtId = UserId.SENTINEL;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<KeyScopeContainer> getScopes() {
      return scopes;
   }

   public void setScopes(List<KeyScopeContainer> scopes) {
      this.scopes = scopes;
   }

   public String getCreationDate() {
      return creationDate;
   }

   public void setCreationDate(String creationDate) {
      this.creationDate = creationDate;
   }

   public String getExpirationDate() {
      return expirationDate;
   }

   public void setExpirationDate(String expirationDate) {
      this.expirationDate = expirationDate;
   }

   public String getHashedValue() {
      return hashedValue;
   }

   public void setHashedValue(String hashedValue) {
      this.hashedValue = hashedValue;
   }

   public String getUniqueID() {
      return uniqueID;
   }

   public void setUniqueID(String uniqueID) {
      this.uniqueID = uniqueID;
   }

   @JsonIgnore
   public boolean isExpired() {
      LocalDate currentDate = LocalDate.now();
      LocalDate expirationDate = LocalDate.parse(this.expirationDate, dateFormatter);

      return (currentDate.isEqual(expirationDate) || currentDate.isBefore(expirationDate)) ? false : true;
   }

   @Override
   public String toString() {
      return "ApiKey{" + "name='" + name + '\'' + ", scopes=" + scopes + ", creationDate='" + creationDate + '\'' + ", expirationDate='" + expirationDate + '\'' + ", hashedValue='" + hashedValue + '\'' + ", uniqueID='" + uniqueID + '\'' + '}';
   }

   public UserId getUserArtId() {
      return userArtId;
   }
}
