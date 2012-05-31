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
package org.eclipse.osee.orcs.core.internal.relation;

import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.orcs.core.internal.artifact.RelationContainer;
import org.eclipse.osee.orcs.data.HasLocalId;

public class RelationFactory {

   private final RelationTypeCache relationTypeCache;

   public RelationFactory(RelationTypeCache relationTypeCache) {
      this.relationTypeCache = relationTypeCache;
   }

   public RelationContainer createRelationContainer(HasLocalId artId) {
      return new RelationContainerImpl(artId, relationTypeCache);
   }

}
