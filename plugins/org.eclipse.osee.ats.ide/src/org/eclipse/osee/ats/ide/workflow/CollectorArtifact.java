/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.workflow;

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.HasMembers;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Donald G. Dunne
 */
public abstract class CollectorArtifact extends AbstractWorkflowArtifact implements HasMembers {

   private final RelationTypeSide membersRelationType;

   public CollectorArtifact(Long id, String guid, BranchId branch, ArtifactTypeToken artifactType, RelationTypeSide membersRelationType) {
      super(id, guid, branch, artifactType);
      this.membersRelationType = membersRelationType;
   }

   @Override
   public void addMember(ArtifactId artifact) {
      if (!getMembers().contains(artifact)) {
         addRelation(USER_DEFINED, membersRelationType,
            AtsApiService.get().getQueryServiceIde().getArtifact(artifact));
      }
   }

}
