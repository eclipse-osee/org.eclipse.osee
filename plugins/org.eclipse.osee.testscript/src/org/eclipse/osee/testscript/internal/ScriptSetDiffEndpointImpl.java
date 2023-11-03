/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.testscript.ScriptResultApi;
import org.eclipse.osee.testscript.ScriptSetDiffEndpoint;

/**
 * @author Ryan T. Baldwin
 */
public class ScriptSetDiffEndpointImpl implements ScriptSetDiffEndpoint {

   private final ScriptResultApi resultApi;
   private final BranchId branch;
   public ScriptSetDiffEndpointImpl(BranchId branch, ScriptResultApi resultApi) {
      this.resultApi = resultApi;
      this.branch = branch;
   }

   @Override
   public Collection<SetDiffToken> getSetDiffs(ArtifactId viewId, List<ArtifactId> ciSets) {
      List<SetDiffToken> results = new LinkedList<>();
      if (ciSets == null || ciSets.isEmpty()) {
         return results;
      }
      Map<String, SetDiffToken> diffs = new HashMap<>();
      for (ArtifactId setId : ciSets) {
         Collection<ScriptResultToken> setResults = resultApi.getAllForSet(branch, viewId, setId);
         for (ScriptResultToken result : setResults) {
            SetDiffToken diff = diffs.getOrDefault(result.getName(), new SetDiffToken(result.getName(), ciSets.size()));
            diff.addResult(setId, result);
            diffs.put(result.getName(), diff);
         }
      }
      List<SetDiffToken> sortedDiffs = new LinkedList<>(diffs.values());
      Collections.sort(sortedDiffs, new Comparator<SetDiffToken>() {

         @Override
         public int compare(SetDiffToken o1, SetDiffToken o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });
      return sortedDiffs;
   }

}
