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
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaRelationTypeFollow extends Criteria {
   private final RelationTypeSide typeSide;
   private final ArtifactTypeToken artifacType;
   private final boolean terminalFollow;

   /**
    * @param terminalFollow true if this is the last (terminal) follow in this chain of follows for this (sub) query
    */
   public CriteriaRelationTypeFollow(RelationTypeSide typeSide, ArtifactTypeToken artifacType, boolean terminalFollow) {
      this.typeSide = typeSide;
      this.artifacType = artifacType;
      this.terminalFollow = terminalFollow;
   }

   public RelationTypeSide getType() {
      return typeSide;
   }

   public ArtifactTypeToken getArtifacType() {
      return artifacType;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNull(typeSide, "Relation Type Side");
   }

   @Override
   public String toString() {
      return "CriteriaRelationTypeFollow [typeSide=" + typeSide + "]";
   }

   public boolean isTerminalFollow() {
      return terminalFollow;
   }
}