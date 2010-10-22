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
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class DiagramNode {

   private String id = GUID.create();
   private String name;
   private final List<DiagramNode> fromPages = new ArrayList<DiagramNode>();
   private final List<DiagramNode> toPages = new ArrayList<DiagramNode>();
   private final List<DiagramNode> returnPages = new ArrayList<DiagramNode>();
   private DiagramNode defaultToPage;
   private String instructionStr;

   @Override
   public int hashCode() {
      int result = 17;
      result = 31 * result + id.hashCode();

      return result;
   }

   public static enum PageType {
      Team,
      ActionableItem
   };
   private PageType pageType;

   public DiagramNode(String name, String id) {
      super();
      this.name = name;
      if (Strings.isValid(id)) {
         this.id = id;
      }
   }

   public DiagramNode() {
      this("", "");
   }

   public String getInstructionStr() {
      return instructionStr;
   }

   public void setInstructionStr(String instructionStr) {
      this.instructionStr = instructionStr;
   }

   public PageType getPageType() {
      return pageType;
   }

   public void setPageType(PageType pageType) {
      this.pageType = pageType;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof DiagramNode) {
         return getId().equals(((DiagramNode) obj).getId());
      }
      return false;
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer(name + (id != null ? " (" + id + ") " : "") + "\n");
      for (DiagramNode page : toPages) {
         sb.append("-> ");
         sb.append(page.name);
         sb.append((returnPages.contains(page) ? " (return)" : ""));
         sb.append("\n");
      }
      return sb.toString();
   }

   public void addFromPage(DiagramNode page) {
      fromPages.add(page);
   }

   public void addToPage(DiagramNode page, boolean returnPage) {
      toPages.add(page);
      if (returnPage) {
         returnPages.add(page);
      }
   }

   public String getName() {
      return name;
   }

   public String getId() {
      return id;
   }

   public List<DiagramNode> getFromPages() {
      return fromPages;
   }

   public List<DiagramNode> getToPages() {
      return toPages;
   }

   public boolean isReturnPage(DiagramNode page) {
      return returnPages.contains(page);
   }

   public DiagramNode getDefaultToPage() {
      return defaultToPage;
   }

   public void setDefaultToPage(DiagramNode defaultToPage) {
      this.defaultToPage = defaultToPage;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<DiagramNode> getReturnPages() {
      return returnPages;
   }

}
