/*
 * Created on Nov 19, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world.search;

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.search.WorldSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

public class MyReviewSearchItem extends WorldSearchItem {

   public MyReviewSearchItem() {
      super("My Reviews");
   }

   @Override
   public WorldUISearchItem copy() {
      AtsSearchData data = getData();
      if (!Strings.isValid(data.getUserId())) {
         data.getWorkItemTypes().add(WorkItemType.Review);
         data.setUserId(AtsClientService.get().getUserService().getCurrentUserId());
         data.setUserType(AtsSearchUserType.Assignee);
      }
      return new WorldSearchItem(data);
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsImage.REVIEW);
   }

}
