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

package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.orcs.core.ds.RelationTypeCriteria;

/**
 * @author Roberto E. Escobar
 */
public final class CriteriaRelationTypeFollow extends RelationTypeCriteria<RelationTypeSide> {
   private final RelationTypeSide typeSide;
   private final ArtifactTypeToken artifacType;
   private final boolean terminalFollow;
   private final boolean useAnotherTable;

   /**
    * @param terminalFollow true if this is the last (terminal) follow in this chain of follows for this (sub) query
    */
   public CriteriaRelationTypeFollow(RelationTypeSide typeSide, ArtifactTypeToken artifacType, boolean terminalFollow, boolean useAnotherTable) {
      super(typeSide);
      this.typeSide = typeSide;
      this.artifacType = artifacType;
      this.terminalFollow = terminalFollow;
      this.useAnotherTable = useAnotherTable;
   }

   public ArtifactTypeToken getArtifacType() {
      return artifacType;
   }

   public boolean isTerminalFollow() {
      return terminalFollow;
   }

   public boolean useAnotherTable() {
      return this.useAnotherTable;
   }
}