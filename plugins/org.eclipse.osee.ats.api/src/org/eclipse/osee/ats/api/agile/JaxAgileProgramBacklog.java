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

import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class JaxAgileProgramBacklog extends JaxAgileProgramObject implements IAgileProgramBacklog {

   public static JaxAgileProgramBacklog construct(IAgileProgram agileProgram, ArtifactToken artifact) {
      JaxAgileProgramBacklog programBacklog = new JaxAgileProgramBacklog();
      programBacklog.setName(artifact.getName());
      programBacklog.setId(artifact.getId());
      programBacklog.setProgramId(agileProgram.getId());
      return programBacklog;
   }

   @Override
   public List<Long> getBacklogItemIds() {
      return null;
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.AgileBacklog;
   }

}
