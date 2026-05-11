/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsQueryServiceServer {

   /**
    * Run query that returns art_ids of IAtsWorkItems to return
    */
   Collection<IAtsWorkItem> getWorkItemsFromQuery(String query, Object... data);

   /**
    * Run query and return list of column,value of results. Results will be key,value where key = column_name in upper
    * case.
    */
   List<Map<String, String>> query(String query, Object... data);

   void runUpdate(String query, Object... data);

   Collection<ArtifactToken> getArtifactsFromQuery(String query, Object... data);

   List<ArtifactId> getArtifactIdsFromQuery(String query, Object... data);

}
