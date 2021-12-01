/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow.cr.bit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class BuildImpactDatas {

   private Collection<BuildImpactData> buildImpacts = new ArrayList<>();
   private ArtifactToken teamWf;
   @JsonIgnore
   private Map<ArtifactToken, BuildImpactData> teamWfToBidMap = new HashMap<>();
   private final XResultData results = new XResultData();
   TransactionId transaction;
   private ArtifactTypeToken bidArtType = ArtifactTypeToken.SENTINEL;

   public BuildImpactDatas() {
      // for jax-rs
   }

   public Collection<BuildImpactData> getBuildImpacts() {
      return buildImpacts;
   }

   public void setBuildImpacts(Collection<BuildImpactData> buildImpacts) {
      this.buildImpacts = buildImpacts;
   }

   @JsonIgnore
   public void addBuildImpactData(BuildImpactData buildImpact) {
      buildImpacts.add(buildImpact);
   }

   public ArtifactToken getTeamWf() {
      return teamWf;
   }

   public void setTeamWf(ArtifactToken teamWf) {
      this.teamWf = teamWf;
   }

   @JsonIgnore
   public Map<ArtifactToken, BuildImpactData> getTeamWfToBidMap() {
      return teamWfToBidMap;
   }

   public void setTeamWfToBidMap(Map<ArtifactToken, BuildImpactData> teamWfToBidMap) {
      this.teamWfToBidMap = teamWfToBidMap;
   }

   public XResultData getResults() {
      return results;
   }

   public TransactionId getTransaction() {
      return transaction;
   }

   public void setTransaction(TransactionId transaction) {
      this.transaction = transaction;
   }

   public ArtifactTypeToken getBidArtType() {
      return bidArtType;
   }

   public void setBidArtType(ArtifactTypeToken bidArtType) {
      this.bidArtType = bidArtType;
   }

}
