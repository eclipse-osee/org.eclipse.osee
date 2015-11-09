/*
 * Created on Oct 22, 2014
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import org.eclipse.osee.activity.api.ActivityEntryId;
import org.eclipse.osee.activity.api.ActivityType;
import org.eclipse.osee.framework.ui.plugin.util.ActivityLogJaxRsService;

public class AtsActivityLogUtil {

   public static ActivityEntryId create(ActivityType type, long parent, Integer initialStatus, String message) {
      return ActivityLogJaxRsService.create(type, parent, initialStatus, message);
   }

   public static void update(ActivityEntryId entryId, Integer statusId) {
      ActivityLogJaxRsService.update(entryId, statusId);
   }
}
