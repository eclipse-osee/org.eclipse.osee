/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.agile;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class JaxAgileProgram extends JaxAgileProgramObject implements IAgileProgram {

   List<Long> teamIds = new LinkedList<>();

   public JaxAgileProgram() {
      // for jax-rs
   }

   @Override
   public List<Long> getTeamIds() {
      return teamIds;
   }

   public void setTeamids(List<Long> teamIds) {
      this.teamIds = teamIds;
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.AgileProgram;
   }

}
