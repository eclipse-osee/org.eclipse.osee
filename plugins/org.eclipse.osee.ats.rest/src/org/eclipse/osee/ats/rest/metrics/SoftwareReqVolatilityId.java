/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.ats.rest.metrics;

/**
 * @author Stephen J. Molaro
 */
public enum SoftwareReqVolatilityId {
   ACT("Action ID"),
   ActionName("Action Name"),
   Program("Program"),
   Build("Build"),
   Date("Date"),
   Completed("Completed Date"),
   Added("Added"),
   Modified("Modified"),
   Deleted("Deleted"),

   Week("Week"),
   Month("Month");

   private final String displayName;

   private SoftwareReqVolatilityId(String displayName) { //
      this.displayName = displayName;

   }

   public String getDisplayName() {
      return this.displayName;
   }
}
