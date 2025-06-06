/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import java.util.ArrayList;
import java.util.List;

public class WorldEditorContributors {

   private static List<IWorldEditorContributor> contributors = new ArrayList<>();

   public WorldEditorContributors() {
      // for osgi
   }

   public void addWorldContributor(IWorldEditorContributor contributor) {
      contributors.add(contributor);
   }

   public static List<IWorldEditorContributor> getContributors() {
      return contributors;
   }
}
