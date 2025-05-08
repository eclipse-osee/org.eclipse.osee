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
package org.eclipse.osee.framework.core.applicability;

/**
 * Used by {@link ApplicabilityParseSubstituteAndSanitize} Java-Rust FFI
 */
public class BatMatchText {
   private String matchText = "";
   private String substitute = "";

   public BatMatchText() {
   }

   /**
    * @return the matchText
    */
   public String getMatchText() {
      return matchText;
   }

   /**
    * @param matchText the matchText to set
    */
   public void setMatchText(String matchText) {
      this.matchText = matchText;
   }

   /**
    * @return the substitute
    */
   public String getSubstitute() {
      return substitute;
   }

   /**
    * @param substitute the substitute to set
    */
   public void setSubstitute(String substitute) {
      this.substitute = substitute;
   }

}
