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
package org.eclipse.osee.display.presenter;

import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author John Misinco
 */
public interface ArtifactProvider {

   ReadableArtifact getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) throws OseeCoreException;

   ReadableArtifact getArtifactByGuid(IOseeBranch branch, String guid) throws OseeCoreException;

   List<ReadableArtifact> getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase) throws OseeCoreException;
}
