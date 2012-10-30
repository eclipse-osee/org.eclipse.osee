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
package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.QueryOptions;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaRelatedTo extends Criteria<QueryOptions> {
   private final IRelationTypeSide relationType;
   private final Collection<Integer> artifactIds;

   public CriteriaRelatedTo(IRelationTypeSide relationType, Collection<Integer> artifactIds) {
      super();
      this.relationType = relationType;
      this.artifactIds = artifactIds;
   }

   public IRelationTypeSide getType() {
      return relationType;
   }

   public Collection<Integer> getIds() {
      return artifactIds;
   }

   @Override
   public void checkValid(QueryOptions options) throws OseeCoreException {
      super.checkValid(options);
      Conditions.checkNotNull(getType(), "relation type side");
   }

   @Override
   public String toString() {
      return "CriteriaRelatedTo [relationType=" + relationType + ", artifactIds=" + artifactIds + "]";
   }

}
