/*******************************************************************************
 * Copyright (c) 2008 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.traceability.report;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;


/**
 * @author Ryan D. Brooks
 */
public class RequirementStatus implements Comparable<RequirementStatus> {
   private final String requirementName;
   private final String legacyId;
   private final String swEnhancement;
   private final StringBuilder partitionStatuses;
   private final List<Integer> percents;
   private final HashSet<AtsUser> testPocs;

   public RequirementStatus(String requirementName, String legacyId, String swEnhancement) {
      this.requirementName = requirementName;
      this.legacyId = legacyId;
      this.swEnhancement = swEnhancement;
      this.partitionStatuses = new StringBuilder();
      this.percents = new LinkedList<>();
      this.testPocs = new HashSet<>();
   }

   public void addPartitionStatus(int percentComplete, String partition, String resolution) {
      if (partition == null) {
         System.out.println("Missing partiton for " + requirementName + ": " + legacyId);
         partition = "";
      }
      partitionStatuses.append(partition);
      partitionStatuses.append(':');
      partitionStatuses.append(resolution);
      partitionStatuses.append(' ');
      percents.add(percentComplete);
   }

   public String getPartitionStatuses() {
      return partitionStatuses.toString();
   }

   public int getRolledupPercentComplete() {
      int total = 0;
      for (int percent : percents) {
         total += percent;
      }
      return total / percents.size();
   }

   public void setTestPocs(Collection<AtsUser> poc) {
      testPocs.addAll(poc);
   }

   public HashSet<AtsUser> getTestPocs() {
      return testPocs;
   }

   public String getLegacyId() {
      return legacyId;
   }

   @Override
   public int compareTo(RequirementStatus status) {
      if (legacyId == null) {
         return -1;
      }
      return legacyId.compareTo(status.legacyId);
   }

   public String getSwEnhancement() {
      return swEnhancement;
   }
}
