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
import java.util.List;
import org.eclipse.osee.ats.api.config.JaxTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class BuildImpactData {

   private ArtifactToken bidArt = ArtifactToken.SENTINEL;
   private ArtifactToken program = ArtifactToken.SENTINEL;
   private ArtifactToken build = ArtifactToken.SENTINEL;
   private String state = BuildImpactState.Open.getName();
   private List<ArtifactToken> configs = new ArrayList<ArtifactToken>();
   private List<JaxTeamWorkflow> teamWfs = new ArrayList<>();
   @JsonIgnore
   private BuildImpactDatas bids;

   public ArtifactToken getBuild() {
      return build;
   }

   public void addTeamWorkflow(JaxTeamWorkflow teamWf) {
      teamWfs.add(teamWf);
      bids.getTeamWfToBidMap().put(teamWf.getToken(), this);
   }

   public void setBuild(ArtifactToken build) {
      this.build = build;
   }

   public List<JaxTeamWorkflow> getTeamWfs() {
      return teamWfs;
   }

   public void setTeamWfs(List<JaxTeamWorkflow> teamWfs) {
      this.teamWfs = teamWfs;
   }

   public ArtifactToken getBidArt() {
      return bidArt;
   }

   public void setBidArt(ArtifactToken bidArt) {
      this.bidArt = bidArt;
   }

   public ArtifactToken getProgram() {
      return program;
   }

   public void setProgram(ArtifactToken program) {
      this.program = program;
   }

   @JsonIgnore
   public BuildImpactDatas getBids() {
      return bids;
   }

   public void setBids(BuildImpactDatas bids) {
      this.bids = bids;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }

   public List<ArtifactToken> getConfigs() {
      return configs;
   }

   public void setConfigs(List<ArtifactToken> configs) {
      this.configs = configs;
   }

   public void addConfig(ArtifactToken config) {
      this.configs.add(config);
   }

}
