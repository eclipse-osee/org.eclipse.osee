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
package org.eclipse.osee.orcs;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author Andrew M. Finkbeiner
 */
public interface Graph {

   Collection<IRelationTypeSide> getExistingRelationTypes(ReadableArtifact art);

   RelationType getFullRelationType(IRelationTypeSide relationTypeSide) throws OseeCoreException;

   List<ReadableArtifact> getRelatedArtifacts(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException;

   List<RelationType> getValidRelationTypes(ReadableArtifact art) throws OseeCoreException;

   int getRelationSideMax(RelationType relationType, IArtifactType artifactType, RelationSide relationSide) throws OseeCoreException;

   ReadableArtifact getRelatedArtifact(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException;

   /**
    * @param art
    * @return the parent artifact if one exists or null if there is no parent
    * @throws OseeCoreException
    */
   ReadableArtifact getParent(ReadableArtifact art) throws OseeCoreException;

   List<ReadableArtifact> getChildren(ReadableArtifact art) throws OseeCoreException;

}
