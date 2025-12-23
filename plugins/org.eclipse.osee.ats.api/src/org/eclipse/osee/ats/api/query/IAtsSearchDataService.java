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

package org.eclipse.osee.ats.api.query;

import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * Supports saving and loading AtsSearchData searches
 *
 * @author Donald G. Dunne
 */
public interface IAtsSearchDataService {

   List<AtsSearchData> getSavedSearches(String namespace);

   TransactionId saveSearch(AtsSearchData data, ArtifactToken user);

   TransactionId removeSearch(AtsSearchData data, ArtifactToken user);

   AtsSearchData getSearch(AtsUser atsUser, Long id);

   AtsSearchData getSearch(String jsonStr);

   AtsSearchData createSearchData(String namespace, String searchName);

}
