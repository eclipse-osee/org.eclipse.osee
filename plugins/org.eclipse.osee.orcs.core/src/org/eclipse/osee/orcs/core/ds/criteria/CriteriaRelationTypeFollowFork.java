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

package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.orcs.core.ds.RelationTypeCriteria;

/**
 * @author Audrey Denk
 */
public final class CriteriaRelationTypeFollowFork extends RelationTypeCriteria<RelationTypeSide> {
   private final RelationTypeSide typeSide;
   private final ArtifactTypeToken artifacType;
   private final String sourceTable;
   private final boolean terminalFollow;

   /**
    * @param terminalFollow true if this is the last (terminal) follow in this chain of follows for this (sub) query
    */
   public CriteriaRelationTypeFollowFork(RelationTypeSide typeSide, ArtifactTypeToken artifacType, String sourceTable, boolean terminalFollow) {
      super(typeSide);
      this.typeSide = typeSide;
      this.artifacType = artifacType;
      this.sourceTable = sourceTable;
      this.terminalFollow = terminalFollow;
   }

   public ArtifactTypeToken getArtifacType() {
      return artifacType;
   }

   public boolean isTerminalFollow() {
      return terminalFollow;
   }

   public String getSourceTable() {
      return sourceTable;
   }
}