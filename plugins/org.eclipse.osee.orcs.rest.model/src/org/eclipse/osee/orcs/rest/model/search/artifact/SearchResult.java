/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.rest.model.search.artifact;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public interface SearchResult {

   int getTotal();

   long getSearchTime();

   String getVersion();

   SearchParameters getSearchParameters();

   List<ArtifactId> getIds();

   List<SearchMatch> getSearchMatches();

}
