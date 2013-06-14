/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.relation;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationContainerImpl implements RelationContainer {

   private final RelationRowCollection rows;

   public RelationContainerImpl(int parentId, RelationTypes relationTypeCache) {
      this.rows = new RelationRowCollection(parentId, relationTypeCache);
   }

   @Override
   public void add(RelationData nextRelation) throws OseeCoreException {
      rows.add(nextRelation);
   }

   @Override
   public void getArtifactIds(Collection<Integer> results, IRelationTypeSide relationTypeSide) {
      rows.getArtifactIds(results, relationTypeSide);
   }

   @Override
   public int getRelationCount(IRelationTypeSide relationTypeSide) {
      return rows.getArtifactCount(relationTypeSide);
   }

   @Override
   public Collection<IRelationTypeSide> getExistingRelationTypes() {
      return rows.getRelationTypes();
   }
}
