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

package org.eclipse.osee.testscript;

import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;

public class DeleteResultsData {
   private final List<ResultToPurge> results;
   private final Set<ArtifactId> ciSets;

   public DeleteResultsData(List<ResultToPurge> results, Set<ArtifactId> ciSets) {
      this.results = results;
      this.ciSets = ciSets;
   }

   public List<ResultToPurge> getResults() {
      return results;
   }

   public Set<ArtifactId> getCiSets() {
      return ciSets;
   }

}
