/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlow;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkFlow extends WorkFlow {

   private String inheritData;

   public AtsWorkFlow(String id) {
      super(id);
   }

   // artifact, etc)

   public AtsWorkPage getAtsPage(String pageName) {
      return (AtsWorkPage) super.getPage(pageName);
   }

   public List<AtsWorkPage> getAtsPages(String pageName) {
      List<AtsWorkPage> foundPages = new ArrayList<AtsWorkPage>();
      for (WorkPage page : super.getPages(pageName))
         foundPages.add((AtsWorkPage) page);
      return foundPages;
   }

   public WorkPage getAtsPageFromId(String id) {
      return (AtsWorkPage) super.getPageFromId(id);
   }

   public Collection<AtsWorkPage> getAtsWorkPages() {
      List<AtsWorkPage> pages = new ArrayList<AtsWorkPage>();
      for (WorkPage page : super.getPages())
         pages.add((AtsWorkPage) page);
      return pages;
   }

   public List<AtsWorkPage> getPagesOrdered() {
      for (WorkPage wpg : pages) {
         AtsWorkPage page = (AtsWorkPage) wpg;
         if (page.isStartPage()) {
            List<AtsWorkPage> orderedPages = new ArrayList<AtsWorkPage>();
            getOrderedPages(page, orderedPages);
            // Move completed to the end if it exists
            AtsWorkPage completedPage = null;
            for (AtsWorkPage wPage : orderedPages)
               if (wPage.getName().equals(DefaultTeamState.Completed.name())) completedPage = wPage;
            if (completedPage != null) {
               orderedPages.remove(completedPage);
               orderedPages.add(completedPage);
            }
            //            for (WorkPage wPage : orderedPages)
            //               System.out.println("Ordered Page: - " + wPage);
            return orderedPages;
         }
      }
      throw new IllegalArgumentException(
            "Can't locate root page for workflow.  Start page must have \"StartPage\" identifier.");
   }

   private void getOrderedPages(AtsWorkPage page, List<AtsWorkPage> pages) {
      // Add this page first
      if (!pages.contains(page)) pages.add(page);
      // Add default page
      if (page.getDefaultToPage() != null) getOrderedPages((AtsWorkPage) page.getDefaultToPage(), pages);
      // Add remaining pages
      for (WorkPage wPage : page.getToPages())
         if (!pages.contains(wPage)) getOrderedPages((AtsWorkPage) wPage, pages);
   }

   /**
    * @return the inheritData
    */
   public String getInheritData() {
      return inheritData;
   }

   /**
    * @param inheritData the inheritData to set
    */
   public void setInheritData(String inheritData) {
      this.inheritData = inheritData;
   }

}
