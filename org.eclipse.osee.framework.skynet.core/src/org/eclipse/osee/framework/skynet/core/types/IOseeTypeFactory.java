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

import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeTypeFactory {

   public ArtifactType createArtifactType(ArtifactTypeCache cache, String guid, boolean isAbstract, String name) throws OseeCoreException;

   public AttributeType createAttributeType(AttributeTypeCache cache, String guid, String name, String baseAttributeTypeId, String attributeProviderNameId, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends IAttributeDataProvider> providerAttributeClass, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String tipText, String taggerId) throws OseeCoreException;

   public RelationType createRelationType(RelationTypeCache cache, String guid, String name, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, boolean isUserOrdered, String defaultOrderTypeGuid) throws OseeCoreException;

   public OseeEnumType createEnumType(OseeEnumTypeCache cache, String guid, String name) throws OseeCoreException;

   public OseeEnumEntry createEnumEntry(OseeEnumTypeCache cache, String guid, String name, int ordinal) throws OseeCoreException;

   // public Branch createBranch(BranchCache cache, String guid, String name, int parentTxNumber, int associatedArtifactId, BranchType branchType, BranchState branchState, boolean isArchived) throws OseeCoreException;
}
