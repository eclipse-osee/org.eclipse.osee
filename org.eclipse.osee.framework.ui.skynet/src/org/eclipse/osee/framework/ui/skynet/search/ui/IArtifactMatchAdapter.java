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
package org.eclipse.osee.framework.ui.skynet.search.ui;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchResult;
import org.eclipse.search.ui.text.Match;

public interface IArtifactMatchAdapter {
   /**
    * Returns an array with all matches contained in the given artifact in the given search result. If the matches are
    * not contained within an <code>Artifact</code>, this method must return an empty array.
    * 
    * @param result the search result to find matches in
    * @param artifact the artifact to find matches in
    * @return an array of matches (possibly empty)
    */
   public abstract Match computeContainedMatch(AbstractArtifactSearchResult result, Artifact artifact);

   /**
    * Returns the artifact associated with the given element (usually the artifact the element is contained in). If the
    * element is not associated with a artifact, this method should return <code>null</code>.
    * 
    * @param element an element associated with a match
    * @return the artifact associated with the element or <code>null</code>
    */
   public abstract Artifact getArtifact(Object element);
}
