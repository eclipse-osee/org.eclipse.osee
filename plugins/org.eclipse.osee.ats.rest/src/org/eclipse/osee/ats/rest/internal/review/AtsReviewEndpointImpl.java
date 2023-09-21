/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.rest.internal.review;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.Path;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.AtsReviewEndpointApi;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;

/**
 * @author Paul A. Garcia
 */
@Path("review")
public class AtsReviewEndpointImpl implements AtsReviewEndpointApi {

   private final AtsApi atsApi;

   public AtsReviewEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public IAtsAbstractReview getReview(String id) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAnyId(id);
      if (workItem == null || !workItem.isReview()) {
         throw new UnsupportedOperationException();
      }

      return (IAtsAbstractReview) workItem;
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(String ids) {
      List<IAtsWorkItem> workItems = atsApi.getQueryService().getWorkItemsByIds(ids);
      List<IAtsAbstractReview> reviews = new ArrayList<>();

      workItems.stream().filter(w -> w.isReview()).forEach(r -> reviews.add((IAtsAbstractReview) r));

      return reviews;
   }

}
