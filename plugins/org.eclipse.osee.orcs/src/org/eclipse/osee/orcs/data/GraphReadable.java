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
package org.eclipse.osee.orcs.data;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.Readable;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.RelationType;

public interface GraphReadable extends Readable {

   Collection<IRelationTypeSide> getExistingRelationTypes(ArtifactReadable art);

   List<RelationType> getValidRelationTypes(ArtifactReadable art) throws OseeCoreException;

   ///////
   ArtifactReadable getParent(ArtifactReadable art) throws OseeCoreException;

   RelationsReadable getChildren(ArtifactReadable art) throws OseeCoreException;

   RelationsReadable getRelatedArtifacts(IRelationTypeSide relationTypeSide, ArtifactReadable art) throws OseeCoreException;

   RelationType getFullRelationType(IRelationTypeSide relationTypeSide) throws OseeCoreException;

   int getRelationSideMax(RelationType relationType, IArtifactType artifactType, RelationSide relationSide) throws OseeCoreException;

}
