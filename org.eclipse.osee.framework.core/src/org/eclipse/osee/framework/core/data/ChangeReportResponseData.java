/*
 * Created on Nov 16, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

import java.util.Collection;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportResponseData {
   
   private final Collection<ChangeItem> changeItems;

   public Collection<ChangeItem> getChangeItems() {
      return changeItems;
   }

   public ChangeReportResponseData(Collection<ChangeItem> changeItems) {
      super();
      this.changeItems = changeItems;
   }
   
   public boolean wasSuccessful(){
      return !changeItems.isEmpty();
   }
}
