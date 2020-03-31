/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

   /**
    * @param terminalFollow true if this is the last (terminal) follow in this chain of follows for this (sub) query
    */
   public CriteriaRelationTypeFollow(RelationTypeSide typeSide, ArtifactTypeToken artifacType, boolean terminalFollow) {
      super(typeSide);
      this.typeSide = typeSide;
      this.artifacType = artifacType;
      this.terminalFollow = terminalFollow;
   }

   public ArtifactTypeToken getArtifacType() {
      return artifacType;
   }

   public boolean isTerminalFollow() {
      return terminalFollow;
   }
}