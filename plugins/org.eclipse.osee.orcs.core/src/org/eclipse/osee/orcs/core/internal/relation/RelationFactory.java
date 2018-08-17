/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.relation;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.RelationDataFactory;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.relation.impl.RelationNodeAdjacencies;
import org.eclipse.osee.orcs.core.internal.util.OrcsConditions;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Roberto E. Escobar
 */
public class RelationFactory {

   private final RelationTypes relationTypes;
   private final RelationDataFactory relationDataFactory;

   public RelationFactory(RelationTypes relationTypes, RelationDataFactory relationDataFactory) {
      this.relationTypes = relationTypes;
      this.relationDataFactory = relationDataFactory;
   }

   public RelationNodeAdjacencies createRelationContainer() {
      return new RelationNodeAdjacencies();
   }

   public Relation createRelation(RelationData data) {
      return new Relation(relationTypes, data);
   }

   public Relation createRelation(Artifact aNode, RelationTypeToken type, Artifact bNode) {
      return createRelation(aNode, type, bNode, "");
   }

   public Relation createRelation(Artifact aNode, RelationTypeToken type, Artifact bNode, String rationale) {
      OrcsConditions.checkBranch(aNode, bNode);
      OrcsConditions.checkRelateSelf(aNode, bNode);
      RelationData data = relationDataFactory.createRelationData(type, aNode.getBranch(), aNode, bNode, rationale);
      return createRelation(data);
   }

   public Relation clone(Relation src) {
      RelationData data = relationDataFactory.clone(src.getOrcsData());
      return createRelation(data);
   }

   public Relation introduce(BranchId branch, RelationData data) {
      RelationData source = relationDataFactory.introduce(branch, data);
      return createRelation(source);
   }
}
