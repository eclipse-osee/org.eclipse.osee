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
package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface RelationContainer {

   void add(RelationRow nextRelation) throws OseeCoreException;

   //   void getArtifactIds(List<Integer> results, long relationTypeUUId, RelationSide side);

   //   int getRelationCount(long relationTypeUUId, RelationSide side);

   Collection<IRelationTypeSide> getAvailableRelationTypes();

   void getArtifactIds(List<Integer> results, IRelationTypeSide relationTypeSide);

   int getRelationCount(IRelationTypeSide relationTypeSide);
}
