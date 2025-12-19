/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.orcs.search.ds.criteria;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.orcs.search.ds.RelationTypeSideCriteria;

/**
 * @author Audrey Denk
 */
public final class CriteriaRelationTypeFollowFork extends RelationTypeSideCriteria {
   private ArtifactTypeToken artifactType;
   private String sourceTable;
   private boolean terminalFollow;

   public CriteriaRelationTypeFollowFork() {
      super(RelationTypeSide.SENTINEL);
      // for jax-rs
   }

   /**
    * @param terminalFollow true if this is the last (terminal) follow in this chain of follows for this (sub) query
    */
   public CriteriaRelationTypeFollowFork(RelationTypeSide typeSide, ArtifactTypeToken artifactType, String sourceTable, boolean terminalFollow) {
      super(typeSide);
      this.sourceTable = sourceTable;
      this.artifactType = artifactType;
      this.terminalFollow = terminalFollow;
   }

   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   public String getSourceTable() {
      return sourceTable;
   }

   public void setSourceTable(String sourceTable) {
      this.sourceTable = sourceTable;
   }

   public boolean isTerminalFollow() {
      return terminalFollow;
   }

   public void setTerminalFollow(boolean terminalFollow) {
      this.terminalFollow = terminalFollow;
   }

}