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
package org.eclipse.osee.ats.api.agile;

import java.util.List;
import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class JaxAgileProgramBacklog extends JaxAtsObject implements IAgileProgramBacklog {

   private Long programId;

   @Override
   public Long getProgramId() {
      return programId;
   }

   public void setProgramId(long programId) {
      this.programId = programId;
   }

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

}
