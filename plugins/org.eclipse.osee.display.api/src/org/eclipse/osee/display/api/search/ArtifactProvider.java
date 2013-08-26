/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.api.search;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author John R. Misinco
 */
public interface ArtifactProvider {

   ArtifactReadable getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) throws OseeCoreException;

   ArtifactReadable getArtifactByGuid(IOseeBranch branch, String guid) throws OseeCoreException;

   ResultSet<ArtifactReadable> getRelatedArtifacts(ArtifactReadable art, IRelationTypeSide relationTypeSide) throws OseeCoreException;

   ArtifactReadable getRelatedArtifact(ArtifactReadable art, IRelationTypeSide relationTypeSide) throws OseeCoreException;

   ArtifactReadable getParent(ArtifactReadable art) throws OseeCoreException;

   Collection<? extends IRelationType> getValidRelationTypes(ArtifactReadable art) throws OseeCoreException;

   String getSideAName(IRelationType type) throws OseeCoreException;

   String getSideBName(IRelationType type) throws OseeCoreException;

   void getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase, final AsyncSearchListener callback) throws OseeCoreException;

   void cancelSearch();
}
