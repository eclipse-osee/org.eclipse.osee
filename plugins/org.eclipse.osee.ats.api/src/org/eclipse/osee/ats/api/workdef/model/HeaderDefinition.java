/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.api.workdef.model;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Donald G. Dunne
 */
public class HeaderDefinition {

   private final List<LayoutItem> layoutItems = new ArrayList<>(5);
   private final WorkDefinition workDefinition;
   private boolean showMetricsHeader = true;
   private boolean showWorkPackageHeader = true;
   private boolean showSiblingLinks = true;

   public boolean isShowWorkPackageHeader() {
      return showWorkPackageHeader;
   }

   public void setShowWorkPackageHeader(boolean showWorkPackageHeader) {
      this.showWorkPackageHeader = showWorkPackageHeader;
   }

   public HeaderDefinition(WorkDefinition workDefinition) {
      this.workDefinition = workDefinition;
   }

   public List<LayoutItem> getLayoutItems() {
      return layoutItems;
   }

   public boolean isShowMetricsHeader() {
      return showMetricsHeader;
   }

   public void setShowMetricsHeader(boolean showMetricsHeader) {
      this.showMetricsHeader = showMetricsHeader;
   }

   public WorkDefinition getWorkDefinition() {
      return workDefinition;
   }

   @Override
   public String toString() {
      return "Header Definition";
   }

   public boolean isShowSiblingLinks() {
      return showSiblingLinks;
   }

   public void setShowSiblingLinks(boolean showSiblingLinks) {
      this.showSiblingLinks = showSiblingLinks;
   }

}
