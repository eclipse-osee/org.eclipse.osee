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
package org.eclipse.osee.ats.rest.internal.report;

public class ActualUrl {

   public String actualCrudType;
   public String actualUrl;
   private boolean skip;
   private boolean match = false;
   private String cleanActUrl;
   private Boolean hasParameter;

   public ActualUrl(String actualCrudType, String actualUrl) {
      this.actualCrudType = actualCrudType;
      this.actualUrl = actualUrl;
   }

   public ActualUrl() {
   }

   public String getActualCrudType() {
      return actualCrudType;
   }

   public void setActualCrudType(String actualCrudType) {
      this.actualCrudType = actualCrudType;
   }

   public String getActualUrl() {
      return actualUrl;
   }

   public void setActualUrl(String actualUrl) {
      this.actualUrl = actualUrl;
   }

   @Override
   public String toString() {
      return "RestUrl [actCrud=" + actualCrudType //
         + ", actualUrl=" + actualUrl + "]";
   }

   public void setSkip() {
      skip = true;
   }

   public boolean isSkip() {
      return skip;
   }

   public void setSkip(boolean skip) {
      this.skip = skip;
   }

   public boolean isMatch() {
      return match;
   }

   public void setMatch() {
      this.match = true;
   }

   public String getCleanActUrlStr() {
      if (cleanActUrl == null) {
         cleanActUrl = actualUrl.replaceFirst("^.*=", "");
      }
      return cleanActUrl;
   }

   public boolean hasParameter() {
      if (hasParameter == null) {
         hasParameter = getCleanActUrlStr().contains("/[0-1]+/");
      }
      return hasParameter;
   }

}
