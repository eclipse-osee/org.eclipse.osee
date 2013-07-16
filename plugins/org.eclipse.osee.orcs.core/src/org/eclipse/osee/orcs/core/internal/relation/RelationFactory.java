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

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.RelationDataFactory;
import org.eclipse.osee.orcs.core.internal.relation.impl.RelationNodeAdjacencies;
import org.eclipse.osee.orcs.core.internal.util.OrcsConditions;
import org.eclipse.osee.orcs.core.internal.util.ValueProvider;
import org.eclipse.osee.orcs.core.internal.util.ValueProviderFactory;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Roberto E. Escobar
 */
public class RelationFactory {

   private final RelationTypes relationTypes;
   private final RelationDataFactory relationDataFactory;
   private final ValueProviderFactory providerFactory;

   public RelationFactory(RelationTypes relationTypes, RelationDataFactory relationDataFactory, ValueProviderFactory providerFactory) {
      this.relationTypes = relationTypes;
      this.relationDataFactory = relationDataFactory;
      this.providerFactory = providerFactory;
   }

   public RelationContainer createRelationContainer(int artId) {
      return new RelationContainerImpl(artId, relationTypes);
   }

   public RelationNodeAdjacencies createRelationContainer() {
      return new RelationNodeAdjacencies();
   }

   public Relation createRelation(RelationData data) {
      ValueProvider<Branch, OrcsData> branch = providerFactory.createBranchProvider(data);
      return new Relation(relationTypes, data, branch);
   }

   public Relation createRelation(RelationNode aNode, IRelationType type, RelationNode bNode) throws OseeCoreException {
      OrcsConditions.checkBranch(aNode, bNode);
      OrcsConditions.checkRelateSelf(aNode, bNode);
      IOseeBranch branch = aNode.getBranch();
      RelationData data = relationDataFactory.createRelationData(type, branch, aNode, bNode, Strings.emptyString());
      return createRelation(data);
   }

   public Relation clone(Relation src) throws OseeCoreException {
      RelationData data = relationDataFactory.clone(src.getOrcsData());
      return createRelation(data);
   }
}
