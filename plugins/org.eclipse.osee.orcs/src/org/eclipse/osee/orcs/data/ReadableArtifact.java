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
package org.eclipse.osee.orcs.data;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 */
public interface ReadableArtifact extends Readable, HasVersion, Identifiable {

   int getId();

   IOseeBranch getBranch();

   String getHumanReadableId();

   int getTransactionId();

   IArtifactType getArtifactType();

   Collection<IAttributeType> getAttributeTypes() throws OseeCoreException;

   <T> List<ReadableAttribute<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException;

   String getSoleAttributeAsString(IAttributeType attributeType) throws OseeCoreException;

   Collection<IRelationType> getValidRelationTypes() throws OseeCoreException;

   Collection<IRelationTypeSide> getAvailableRelationTypes() throws OseeCoreException;

   List<ReadableArtifact> getRelatedArtifacts(IRelationTypeSide relationTypeSide, QueryFactory queryFactory) throws OseeCoreException;

   ReadableArtifact getRelatedArtifact(IRelationTypeSide relationTypeSide, QueryFactory queryFactory) throws OseeCoreException;

   boolean hasParent();

   ReadableArtifact getParent();

   @Override
   String toString();
}