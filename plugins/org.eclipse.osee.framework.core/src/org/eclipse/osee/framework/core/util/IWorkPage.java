/*
 * Created on Nov 16, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.util;

public interface IWorkPage {

   public WorkPageType getWorkPageType();

   public String getPageName();

   public String getDescription();

   public Integer getDefaultPercent();

   public boolean isCompletedPage();

   public boolean isCancelledPage();

   public boolean isWorkingPage();

   public boolean isCompletedOrCancelledPage();

}
