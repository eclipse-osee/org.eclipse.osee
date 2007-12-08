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
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Donald G. Dunne
 */
public class WorkFlow {

   protected List<WorkPage> pages = new ArrayList<WorkPage>();
   private Set<String> pageNames = new HashSet<String>();
   private String id;

   public WorkFlow(String id) {
      super();
      this.id = id;
   }

   public Set<String> getPageNames() {
      if (pageNames.size() == 0) {
         for (WorkPage page : pages)
            pageNames.add(page.getName());
      }
      return pageNames;
   }

   public void addPage(WorkPage page) {
      pages.add(page);
   }

   public WorkPage getPage(String pageName) {
      List<WorkPage> pages = getPages(pageName);
      if (pages.size() > 1) throw new IllegalArgumentException(
            "Multiple pages of same name found in workflow => " + getId() + " Use getPages.");
      return pages.iterator().next();
   }

   public List<WorkPage> getPages(String pageName) {
      List<WorkPage> foundPages = new ArrayList<WorkPage>();
      for (WorkPage page : pages)
         if (page.getName().equals(pageName)) foundPages.add(page);
      if (foundPages.size() == 0) throw new IllegalArgumentException(
            "Invalid page Name \"" + pageName + "\" from workflow => " + getId());
      return foundPages;
   }

   public WorkPage getPageFromId(String id) {
      for (WorkPage page : pages)
         if (page.getId().equals(id)) return page;
      return null;
   }

   public Collection<WorkPage> getPages() {
      return pages;
   }

   /**
    * @return the id
    */
   public String getId() {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id) {
      this.id = id;
   }

}
