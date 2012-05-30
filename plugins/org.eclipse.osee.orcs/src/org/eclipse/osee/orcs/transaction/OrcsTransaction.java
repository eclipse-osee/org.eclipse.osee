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
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.WritableArtifact;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsTransaction {

   IOseeBranch getBranch();

   ReadableArtifact getAuthor();

   String getComment();

   void setComment(String comment);

   ////////////////////////

   WritableArtifact asWritable(ReadableArtifact artifact) throws OseeCoreException;

   List<WritableArtifact> asWritable(Collection<? extends ReadableArtifact> artifact) throws OseeCoreException;

   ////////////////////////

   WritableArtifact createArtifact(IArtifactType artifactType, String name) throws OseeCoreException;

   WritableArtifact createArtifact(IArtifactType artifactType, String name, GUID guid) throws OseeCoreException;

   WritableArtifact createArtifact(IArtifactToken artifactToken) throws OseeCoreException;

   WritableArtifact duplicateArtifact(ReadableArtifact sourceArtifact) throws OseeCoreException;

   WritableArtifact duplicateArtifact(ReadableArtifact sourceArtifact, Collection<? extends IAttributeType> attributesToDuplicate) throws OseeCoreException;

   WritableArtifact reflectArtifact(ReadableArtifact sourceArtifact) throws OseeCoreException;

   void deleteArtifact(WritableArtifact artifact) throws OseeCoreException;

   ////////////////////////

   void rollback();

   ITransaction commit() throws OseeCoreException;

}
