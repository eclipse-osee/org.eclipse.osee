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

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeRow;
import org.eclipse.osee.orcs.core.ds.RelationContainer;

public class RelationFactory {

   private final RelationTypeCache relationTypeCache;
   private final Log logger;

   public RelationFactory(Log logger, RelationTypeCache relationTypeCache) {
      this.relationTypeCache = relationTypeCache;
      this.logger = logger;
   }

   public <T> void loadRelation(RelationContainer container, AttributeRow row) throws OseeCoreException {
      RelationType relationType = relationTypeCache.getByGuid(row.getAttrTypeUuid());
      container.add(relationType, createRelation(row));
   }

   private Relation createRelation(AttributeRow row) {
      return new Relation(int aArtifactId, int bArtifactId, RelationType relationType, int relationId, int gammaId, String rationale, ModificationType modificationType);
   }
}
