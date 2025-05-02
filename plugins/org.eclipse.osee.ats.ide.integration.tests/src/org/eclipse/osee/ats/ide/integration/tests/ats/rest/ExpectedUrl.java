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
package org.eclipse.osee.ats.ide.integration.tests.ats.rest;

import java.util.ArrayList;
import java.util.List;

public class ExpectedUrl {

   public List<String> expectedCrudTypes = new ArrayList<>();
   public String expectedUrl;
   private ActualUrl actualMatch;
   private String cleanExpUrlStr;
   private Boolean hasParameter;

   public ExpectedUrl() {
   }

   @Override
   public String toString() {
      return "RestUrl [expectedUrl=" + expectedUrl //
         + ", expectedCruds=" + expectedCrudTypes + "]";
   }

   public void addExpectedCrudType(String match) {
      expectedCrudTypes.add(match);
   }

   public String getExpectedUrl() {
      return expectedUrl;
   }

   public void setExpectedUrl(String expectedUrl) {
      this.expectedUrl = expectedUrl;
   }

   public boolean isMatch() {
      return actualMatch != null;
   }

   public ActualUrl getActualMatch() {
      return actualMatch;
   }

   public List<String> getExpectedCrudTypes() {
      return expectedCrudTypes;
   }

   public void setExpectedCrudTypes(List<String> expectedCrudTypes) {
      this.expectedCrudTypes = expectedCrudTypes;
   }

   public void setActualMatch(ActualUrl actualMatch) {
      this.actualMatch = actualMatch;
   }

   public void setCleanExpUrlStr(String cleanExpUrlStr) {
      this.cleanExpUrlStr = cleanExpUrlStr;
   }

   public String getCleanExpUrlStr() {
      return cleanExpUrlStr;
   }

   public Boolean hasParameter() {
      if (hasParameter == null) {
         hasParameter = expectedUrl.contains("{");
      }
      return hasParameter;
   }

}
