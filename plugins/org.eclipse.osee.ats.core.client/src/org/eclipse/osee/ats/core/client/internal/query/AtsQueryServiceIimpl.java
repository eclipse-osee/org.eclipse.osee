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
package org.eclipse.osee.ats.core.client.internal.query;

import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.query.IAtsWorkItemFilter;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.query.AtsWorkItemFilter;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryServiceIimpl implements IAtsQueryService {

   private final IAtsClient atsClient;

   public AtsQueryServiceIimpl(IAtsClient atsClient) {
      this.atsClient = atsClient;
   }

   @Override
   public IAtsQuery createQuery() {
      return new AtsQueryImpl(atsClient);
   }

   @Override
   public IAtsWorkItemFilter createFilter(List<IAtsWorkItem> workItems) {
      return new AtsWorkItemFilter(workItems, atsClient.getWorkItemService());
   }

}
