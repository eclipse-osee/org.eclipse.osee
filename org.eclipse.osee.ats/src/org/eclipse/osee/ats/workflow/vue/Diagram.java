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
package org.eclipse.osee.ats.workflow.vue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Donald G. Dunne
 */
public class Diagram {

   protected List<DiagramNode> nodes = new ArrayList<DiagramNode>();
   private Set<String> nodeNames = new HashSet<String>();
   private String id;
   private String inheritData;

   public Diagram(String id) {
      super();
      this.id = id;
   }

   public Set<String> getPageNames() {
      if (nodeNames.size() == 0) {
         for (DiagramNode page : nodes)
            nodeNames.add(page.getName());
      }
      return nodeNames;
   }

   public void addPage(DiagramNode page) {
      nodes.add(page);
   }

   public DiagramNode getPage(String pageName) {
      List<DiagramNode> pages = getPages(pageName);
      if (pages.size() > 1) throw new IllegalArgumentException(
            "Multiple node of same name found in workflow => " + getId() + " Use getPages.");
      return pages.iterator().next();
   }

   public List<DiagramNode> getPages(String pageName) {
      List<DiagramNode> foundPages = new ArrayList<DiagramNode>();
      for (DiagramNode page : nodes)
         if (page.getName().equals(pageName)) foundPages.add(page);
      if (foundPages.size() == 0) throw new IllegalArgumentException(
            "Invalid node Name \"" + pageName + "\" from workflow => " + getId());
      return foundPages;
   }

   public DiagramNode getPageFromId(String id) {
      for (DiagramNode page : nodes)
         if (page.getId().equals(id)) return page;
      return null;
   }

   public Collection<DiagramNode> getPages() {
      return nodes;
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
