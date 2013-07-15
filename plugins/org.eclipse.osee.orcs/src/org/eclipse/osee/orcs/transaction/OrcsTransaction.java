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
package org.eclipse.osee.orcs.transaction;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsTransaction {

   IOseeBranch getBranch();

   ArtifactReadable getAuthor();

   String getComment();

   void setComment(String comment);

   ////////////////////////

   ArtifactWriteable asWriteable(ArtifactReadable artifact) throws OseeCoreException;

   List<ArtifactWriteable> asWriteable(Collection<? extends ArtifactReadable> artifact) throws OseeCoreException;

   ////////////////////////

   ArtifactWriteable createArtifact(IArtifactType artifactType, String name) throws OseeCoreException;

   ArtifactWriteable createArtifact(IArtifactType artifactType, String name, String guid) throws OseeCoreException;

   ArtifactWriteable createArtifact(IArtifactToken artifactToken) throws OseeCoreException;

   ArtifactWriteable duplicateArtifact(ArtifactReadable sourceArtifact) throws OseeCoreException;

   ArtifactWriteable duplicateArtifact(ArtifactReadable sourceArtifact, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException;

   ArtifactWriteable introduceArtifact(ArtifactReadable sourceArtifact) throws OseeCoreException;

   ////////////////////////

   void unrelate(ArtifactReadable side1, IRelationTypeSide typeSide, ArtifactReadable side2) throws OseeCoreException;

   ////////////////////////

   TransactionRecord commit() throws OseeCoreException;

   boolean isCommitInProgress();

}
