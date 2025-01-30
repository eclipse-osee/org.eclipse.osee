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
package org.eclipse.osee.orcs.rest.model.transaction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CycleDetectionResult {
   private final List<Set<Integer>> componentsWithCycles;
   private final Set<Integer> cycleNodes;

   public CycleDetectionResult() {
      this.componentsWithCycles = Arrays.asList(new HashSet<>());
      this.cycleNodes = new HashSet<>();
   }

   public CycleDetectionResult(List<Set<Integer>> componentsWithCycles, Set<Integer> cycleNodes) {
      this.componentsWithCycles = componentsWithCycles;
      this.cycleNodes = cycleNodes;
   }

   public List<Set<Integer>> getComponentsWithCycles() {
      return componentsWithCycles;
   }

   public Set<Integer> getCycleNodes() {
      return cycleNodes;
   }
}
