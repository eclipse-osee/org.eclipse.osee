/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface ArtifactTypes extends IdCollection<IArtifactType> {

   boolean hasSuperArtifactTypes(IArtifactType artType) throws OseeCoreException;

   Collection<? extends IArtifactType> getSuperArtifactTypes(IArtifactType artType) throws OseeCoreException;

   Collection<? extends IArtifactType> getAllDescendantTypes(ArtifactTypeId artType) throws OseeCoreException;

   boolean isValidAttributeType(IArtifactType artType, BranchId branch, AttributeTypeId attributeType) throws OseeCoreException;

   Collection<AttributeTypeToken> getAttributeTypes(IArtifactType artType, BranchId branch) throws OseeCoreException;

   boolean isAbstract(IArtifactType artType) throws OseeCoreException;

   boolean inheritsFrom(ArtifactTypeId artType, ArtifactTypeId... otherTypes) throws OseeCoreException;

   Map<BranchId, Collection<AttributeTypeToken>> getAllAttributeTypes(IArtifactType artType) throws OseeCoreException;

}