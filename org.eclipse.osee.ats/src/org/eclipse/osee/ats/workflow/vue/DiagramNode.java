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
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetOptionResolver;

/**
 * @author Donald G. Dunne
 */
public class DiagramNode {

   private String id = GUID.generateGuidStr();
   private String name;
   private ArrayList<DiagramNode> fromPages = new ArrayList<DiagramNode>();
   private ArrayList<DiagramNode> toPages = new ArrayList<DiagramNode>();
   private ArrayList<DiagramNode> returnPages = new ArrayList<DiagramNode>();
   private DiagramNode defaultToPage;
   private String instructionStr;

   public static enum PageType {
      Team, ActionableItem
   };
   private PageType pageType;

   /**
    * @param instructionLines input lines of WorkAttribute declarations
    */
   public DiagramNode(String name, String id, String xWidgetsXml, IXWidgetOptionResolver optionResolver) {
      super();
      this.name = name;
      if (id != null && !id.equals("")) this.id = id;
   }

   public DiagramNode(String xWidgetsXml, IXWidgetOptionResolver optionResolver) {
      this("", "", xWidgetsXml, optionResolver);
   }

   public DiagramNode(IXWidgetOptionResolver optionResolver) {
      this("", "", null, optionResolver);
   }

   /**
    * @return the instructionStr
    */
   public String getInstructionStr() {
      return instructionStr;
   }

   /**
    * @param instructionStr the instructionStr to set
    */
   public void setInstructionStr(String instructionStr) {
      this.instructionStr = instructionStr;
   }

   /**
    * @return Returns the pageType.
    */
   public PageType getPageType() {
      return pageType;
   }

   /**
    * @param pageType The pageType to set.
    */
   public void setPageType(PageType pageType) {
      this.pageType = pageType;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof DiagramNode) return getId().equals(((DiagramNode) obj).getId());
      return false;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer(name + (id != null ? " (" + id + ") " : "") + "\n");
      for (DiagramNode page : toPages) {
         sb.append("-> " + page.name + (returnPages.contains(toPages) ? " (return)" : "") + "\n");
      }
      return sb.toString();
   }

   public void addFromPage(DiagramNode page) {
      fromPages.add(page);
   }

   public void addToPage(DiagramNode page, boolean returnPage) {
      toPages.add(page);
      if (returnPage) returnPages.add(page);
   }

   public String getName() {
      return name;
   }

   public String getId() {
      return id;
   }

   /**
    * @return Returns the fromPages.
    */
   public ArrayList<DiagramNode> getFromPages() {
      return fromPages;
   }

   /**
    * @return Returns the toPages.
    */
   public ArrayList<DiagramNode> getToPages() {
      return toPages;
   }

   public boolean isReturnPage(DiagramNode page) {
      return returnPages.contains(page);
   }

   /**
    * @return Returns the defaultToPage.
    */
   public DiagramNode getDefaultToPage() {
      return defaultToPage;
   }

   /**
    * @param defaultToPage The defaultToPage to set.
    */
   public void setDefaultToPage(DiagramNode defaultToPage) {
      this.defaultToPage = defaultToPage;
   }

   /**
    * @param id The id to set.
    */
   public void setId(String id) {
      this.id = id;
   }

   /**
    * @param name The name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the returnPages
    */
   public ArrayList<DiagramNode> getReturnPages() {
      return returnPages;
   }

}
