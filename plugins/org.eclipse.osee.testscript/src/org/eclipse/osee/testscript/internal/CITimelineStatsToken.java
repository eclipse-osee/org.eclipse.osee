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

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Stephen J. Molaro
 */
public class CITimelineStatsToken {

   private String name;
   private Collection<CIStatsToken> ciStats;

   public CITimelineStatsToken(String name, Collection<CIStatsToken> ciStats) {
      this.name = name;
      this.ciStats = ciStats;
   }

   public CITimelineStatsToken(String name) {
      this.name = name;
      this.ciStats = new ArrayList<>();
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Collection<CIStatsToken> getCiStats() {
      return ciStats;
   }

   public void setCiStats(Collection<CIStatsToken> ciStats) {
      this.ciStats = ciStats;
   }
}
