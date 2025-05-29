/*******************************************************************************
 * Copyright (c) 2025 Boeing.
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
package org.eclipse.osee.ats.api.workflow;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Donald G. Dunne
 */
public class NewActionMemberData {

   private ArtifactId memberArt = ArtifactId.SENTINEL;
   private ArtifactId dropTargetArt = ArtifactId.SENTINEL;
   private RelationTypeToken relationType = null;

   public NewActionMemberData() {
      // for jax-rs
   }

   public ArtifactId getMemberArt() {
      return memberArt;
   }

   public void setMemberArt(ArtifactId memberArt) {
      this.memberArt = memberArt;
   }

   public ArtifactId getDropTargetArt() {
      return dropTargetArt;
   }

   public void setDropTargetArt(ArtifactId dropTargetArt) {
      this.dropTargetArt = dropTargetArt;
   }

   public RelationTypeToken getRelationType() {
      return relationType;
   }

   public void setRelationType(RelationTypeToken relationTypeSide) {
      this.relationType = relationTypeSide;
   }

}
