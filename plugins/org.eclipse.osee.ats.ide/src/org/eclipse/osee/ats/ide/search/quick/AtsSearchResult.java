/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.search.quick;

import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery;
import org.eclipse.osee.framework.ui.skynet.search.ArtifactSearchResult;

/**
 * ATS-specific search result that enables the ATS search result view page with custom label provider.
 *
 * @author Donald G. Dunne
 */
public class AtsSearchResult extends ArtifactSearchResult {

   public AtsSearchResult(AbstractArtifactSearchQuery query) {
      super(query);
   }
}
