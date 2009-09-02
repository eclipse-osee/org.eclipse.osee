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
package org.eclipse.osee.framework.skynet.core.types;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeTypeDataAccessor {

   public void loadAllTypeValidity(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException;

   public void loadAllAttributeTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException;

   public void loadAllRelationTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException;

   public void loadAllArtifactTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException;

   public void loadAllOseeEnumTypes(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException;

   public void storeArtifactType(OseeTypeCache cache, Collection<ArtifactType> artifactType) throws OseeCoreException;

   public void storeAttributeType(Collection<AttributeType> attributeType) throws OseeCoreException;

   public void storeRelationType(Collection<RelationType> relationType) throws OseeCoreException;

   public void storeOseeEnumType(Collection<OseeEnumType> oseeEnumType) throws OseeCoreException;

}
