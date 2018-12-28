/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class NewActionResult {

   ArtifactId action;
   List<ArtifactId> teamWfs = new LinkedList<>();
   XResultData results = new XResultData();

   public NewActionResult() {
      // for jax-rs
   }

   public ArtifactId getAction() {
      return action;
   }

   public void setAction(ArtifactId action) {
      this.action = action;
   }

   public List<ArtifactId> getTeamWfs() {
      return teamWfs;
   }

   public void setTeamWfs(List<ArtifactId> teamWfs) {
      this.teamWfs = teamWfs;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public void addTeamWf(ArtifactId teamWf) {
      teamWfs.add(ArtifactId.valueOf(teamWf));
   }

}
