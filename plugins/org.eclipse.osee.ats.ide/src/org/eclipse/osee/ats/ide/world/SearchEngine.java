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

   AsArtifacts("Search (New)", false),
   IdeClient("Search (Legacy)", false),
   ResultsEditor("Search-Beta (Admin)", true);

   private final String displayName;
   private final boolean admin;

   SearchEngine(String displayName, boolean admin) {
      this.displayName = displayName;
      this.admin = admin;
   }

   public String getDisplayName() {
      return displayName;
   }

   public boolean isAdmin() {
      return admin;
   }
}
