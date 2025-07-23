/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.api.workflow;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Donald G. Dunne
 */
public class NewActionRel {

   private RelationTypeToken relationType;
   private boolean sideA;
   private List<ArtifactId> artifacts;
   private NewActionRelOp operation;

   public NewActionRel() {
      // for jax-rs
   }

   public NewActionRel(RelationTypeSide relationSide, ArtifactId artifact, NewActionRelOp operation) {
      this(relationSide, Arrays.asList(artifact), operation);
   }

   public NewActionRel(RelationTypeSide relationSide, List<ArtifactId> artifacts, NewActionRelOp operation) {
      this.relationType = relationSide.getRelationType();
      this.sideA = relationSide.getSide().isSideA();
      this.artifacts = artifacts;
      this.operation = operation;
   }

   public NewActionRelOp getOperation() {
      return operation;
   }

   public List<ArtifactId> getArtifacts() {
      return artifacts;
   }

   public void setArtifacts(List<ArtifactId> artifacts) {
      this.artifacts = artifacts;
   }

   public void setOperation(NewActionRelOp operation) {
      this.operation = operation;
   }

   public RelationTypeToken getRelationType() {
      return relationType;
   }

   public void setRelationType(RelationTypeToken relationType) {
      this.relationType = relationType;
   }

   public boolean isSideA() {
      return sideA;
   }

   public void setSideA(Boolean sideA) {
      this.sideA = sideA;
   }

}
