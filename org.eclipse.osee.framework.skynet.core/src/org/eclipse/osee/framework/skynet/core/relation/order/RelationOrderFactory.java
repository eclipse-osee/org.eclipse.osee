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
package org.eclipse.osee.framework.skynet.core.relation.order;

import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Roberto E. Escobar
 */
public class RelationOrderFactory {

   public RelationTypeSideSorter createTypeSideSorter(RelationSorterProvider sorterProvider, IArtifact artifact, RelationType type, RelationSide side) throws OseeCoreException {
      RelationOrderData data = createRelationOrderData(artifact);
      return new RelationTypeSideSorter(type, side, sorterProvider, data);
   }

   public RelationOrderData createRelationOrderData(IArtifact artifact) throws OseeCoreException {
      ArtifactRelationOrderAccessor accessor = new ArtifactRelationOrderAccessor(new RelationOrderParser());
      RelationOrderData data = new RelationOrderData(accessor, artifact);
      data.load();
      return data;
   }
}
