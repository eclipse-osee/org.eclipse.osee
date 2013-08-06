/*******************************************************************************
 * Copyright (c) 2012 Boeing.
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
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaRelatedTo extends Criteria {
   private final IRelationTypeSide relationTypeSide;
   private final Collection<Integer> artifactIds;

   public CriteriaRelatedTo(IRelationTypeSide relationTypeSide, Collection<Integer> artifactIds) {
      super();
      this.relationTypeSide = relationTypeSide;
      this.artifactIds = artifactIds;
   }

   public IRelationTypeSide getType() {
      return relationTypeSide;
   }

   public Collection<Integer> getIds() {
      return artifactIds;
   }

   @Override
   public void checkValid(Options options) throws OseeCoreException {
      super.checkValid(options);
      Conditions.checkNotNull(getType(), "relation type side");
   }

   @Override
   public String toString() {
      return "CriteriaRelatedTo [relationTypeSide=" + relationTypeSide + ", artifactIds=" + artifactIds + "]";
   }

}
