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

/**
 * @author Andrew M. Finkbeiner
 */
public interface RelationContainer {

   void add(RelationData nextRelation) throws OseeCoreException;

   Collection<IRelationTypeSide> getExistingRelationTypes();

   void getArtifactIds(Collection<Integer> results, IRelationTypeSide relationTypeSide);

   int getRelationCount(IRelationTypeSide relationTypeSide);

}
