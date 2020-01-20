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

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class JaxAgileProgramBacklogItem extends JaxAgileProgramObject implements IAgileProgramBacklogItem {

   private Long backlogId;

   @Override
   public Long getBacklogId() {
      return backlogId;
   }

   public void setBacklogId(Long backlogId) {
      this.backlogId = backlogId;
   }

   public static JaxAgileProgramBacklogItem construct(IAgileProgramBacklog programBacklog, ArtifactToken artifact) {
      return construct(programBacklog.getId(), artifact);
   }

   public static JaxAgileProgramBacklogItem construct(Long programBacklogId, ArtifactToken artifact) {
      JaxAgileProgramBacklogItem item = new JaxAgileProgramBacklogItem();
      item.setName(artifact.getName());
      item.setId(artifact.getId());
      item.setBacklogId(programBacklogId);
      return item;
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.AgileProgramBacklogItem;
   }

}
