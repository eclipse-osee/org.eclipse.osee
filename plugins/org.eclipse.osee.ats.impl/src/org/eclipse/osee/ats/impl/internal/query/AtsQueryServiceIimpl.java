/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.query;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.query.IAtsWorkItemFilter;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.query.AtsWorkItemFilter;
import org.eclipse.osee.ats.impl.IAtsServer;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryServiceIimpl implements IAtsQueryService {

   private final IAtsServer atsServer;

   public AtsQueryServiceIimpl(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @Override
   public IAtsQuery createQuery(WorkItemType workItemType, WorkItemType... workItemTypes) {
      AtsQueryImpl query = new AtsQueryImpl(atsServer);
      query.isOfType(workItemType);
      for (WorkItemType type : workItemTypes) {
         query.isOfType(type);
      }
      return query;
   }

   @Override
   public IAtsWorkItemFilter createFilter(Collection<? extends IAtsWorkItem> workItems) {
      return new AtsWorkItemFilter(workItems, atsServer.getServices());
   }

   @Override
   public ArrayList<AtsSearchData> getSavedSearches(IAtsUser atsUser, String namespace) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public void saveSearch(IAtsUser atsUser, AtsSearchData data) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public void removeSearch(IAtsUser atsUser, AtsSearchData data) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public AtsSearchData getSearch(IAtsUser atsUser, Long uuid) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

   @Override
   public AtsSearchData getSearch(String jsonStr) {
      throw new UnsupportedOperationException("Unsupported on the server");
   }

}
