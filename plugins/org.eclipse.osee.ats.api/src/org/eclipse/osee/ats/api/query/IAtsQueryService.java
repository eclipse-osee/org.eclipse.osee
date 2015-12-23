/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.query;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.WorkItemType;

/**
 * @author Donald G. Dunne
 */
public interface IAtsQueryService {

   IAtsQuery createQuery(WorkItemType workItemType, WorkItemType... workItemTypes);

   IAtsWorkItemFilter createFilter(Collection<? extends IAtsWorkItem> workItems);

   ArrayList<AtsSearchData> getSavedSearches(IAtsUser atsUser, String namespace);

   void saveSearch(IAtsUser atsUser, AtsSearchData data);

   void removeSearch(IAtsUser atsUser, AtsSearchData data);

   AtsSearchData getSearch(IAtsUser atsUser, Long uuid);

   AtsSearchData getSearch(String jsonStr);

   AtsSearchData createSearchData(String namespace, String searchName);

}
