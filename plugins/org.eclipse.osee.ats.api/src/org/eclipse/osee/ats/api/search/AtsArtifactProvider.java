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
package org.eclipse.osee.ats.api.search;

import java.util.Collection;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author John Misinco
 */
public interface AtsArtifactProvider extends ArtifactProvider {

   Collection<ReadableArtifact> getPrograms() throws OseeCoreException;

   Collection<ReadableArtifact> getBuilds(String programGuid) throws OseeCoreException;

   String getBaselineBranchGuid(String buildArtGuid) throws OseeCoreException;
}
