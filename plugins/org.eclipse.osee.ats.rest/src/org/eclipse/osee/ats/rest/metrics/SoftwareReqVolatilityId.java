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
   ACT("Action Id"),
   TW("Worflow Id"),
   ActionName("Action Name"),
   Program("Program"),
   Build("Build"),
   Date("Creation Date"),
   Completed("Completed Date"),
   AddedReq("Added (Software Reqs)"),
   ModifiedReq("Modified (Software Reqs)"),
   DeletedReq("Deleted (Software Reqs)"),
   AddedImpl("Added (Impl Details)"),
   ModifiedImpl("Modified (Impl Details)"),
   DeletedImpl("Deleted (Impl Details)"),
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
