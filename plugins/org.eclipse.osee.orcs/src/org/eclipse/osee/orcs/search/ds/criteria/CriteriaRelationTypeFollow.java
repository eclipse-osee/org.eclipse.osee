/*********************************************************************
 * Copyright (c) 2014 Boeing
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
 * @author Roberto E. Escobar
 */
public final class CriteriaRelationTypeFollow extends RelationTypeSideCriteria {
   private ArtifactTypeToken artifactType;
   private boolean terminalFollow;
   private boolean useAnotherTable;

   public CriteriaRelationTypeFollow() {
      // for jax-rs
      super(RelationTypeSide.SENTINEL);
   }

   /**
    * @param terminalFollow true if this is the last (terminal) follow in this chain of follows for this (sub) query
    */
   public CriteriaRelationTypeFollow(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType, boolean terminalFollow, boolean useAnotherTable) {
      super(relationTypeSide);
      this.artifactType = artifactType;
      this.terminalFollow = terminalFollow;
      this.useAnotherTable = useAnotherTable;
   }

   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   public boolean isTerminalFollow() {
      return terminalFollow;
   }

   public void setTerminalFollow(boolean terminalFollow) {
      this.terminalFollow = terminalFollow;
   }

   public boolean isUseAnotherTable() {
      return useAnotherTable;
   }

   public void setUseAnotherTable(boolean useAnotherTable) {
      this.useAnotherTable = useAnotherTable;
   }

}