/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.world;

/**
 * @author Donald G. Dunne
 */
public enum SearchEngine {

   AsArtifacts("Search (New)", "New high-performing server based search", false),
   IdeClient("Search (Legacy)", "Legacy client based search being retired", false),
   ResultsEditor("Search-Beta (Admin)", "Non Artifact-Based Search - Admin Only", true);

   private final String displayName;
   private final boolean admin;
   private final String toolTip;

   SearchEngine(String displayName, String toolTip, boolean admin) {
      this.displayName = displayName;
      this.toolTip = toolTip;
      this.admin = admin;
   }

   public String getToolTip() {
      return toolTip;
   }

   public String getDisplayName() {
      return displayName;
   }

   public boolean isAdmin() {
      return admin;
   }
}
