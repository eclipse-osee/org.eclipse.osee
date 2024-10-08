/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.api.program;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class ProgramVersions {
   private ArtifactToken program;
   private ArtifactToken team;
   private List<ArtifactToken> versions = new LinkedList<>();

   public ArtifactToken getProgram() {
      return program;
   }

   public void setProgram(ArtifactToken program) {
      this.program = program;
   }

   public ArtifactToken getTeam() {
      return team;
   }

   public void setTeam(ArtifactToken team) {
      this.team = team;
   }

   public List<ArtifactToken> getVersions() {
      return versions;
   }

   public void addVersion(ArtifactToken version) {
      versions.add(version);
   }

   public void setVersions(List<ArtifactToken> versions) {
      this.versions = versions;
   }
}
