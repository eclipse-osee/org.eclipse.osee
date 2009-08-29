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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;

public interface IArtifactTypeDataAccess {

   public Collection<AttributeType> getAttributeTypesFor(ArtifactType artifactType, Branch branch) throws OseeCoreException;

   public Collection<AttributeType> getAttributeTypesFor(ArtifactType artifactType) throws OseeCoreException;

   public Collection<ArtifactType> getArtifactSuperTypesFor(ArtifactType artifactType) throws OseeCoreException;

   public void setAttributeTypes(ArtifactType artifactType, Collection<AttributeType> attributeTypes, Branch branch) throws OseeCoreException;

   public Collection<ArtifactType> getDescendants(ArtifactType artifactType, boolean recurse) throws OseeCoreException;
}
