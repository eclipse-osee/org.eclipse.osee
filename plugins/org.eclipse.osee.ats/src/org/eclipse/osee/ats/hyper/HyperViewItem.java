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
package org.eclipse.osee.ats.hyper;

import java.util.ArrayList;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.swt.graphics.Image;

public class HyperViewItem {

   private final ArrayList<HyperViewItem> bottoms = new ArrayList<HyperViewItem>(); // bottom
   private final ArrayList<HyperViewItem> tops = new ArrayList<HyperViewItem>(); // top
   private final ArrayList<HyperViewItem> lefts = new ArrayList<HyperViewItem>(); // left
   private final ArrayList<HyperViewItem> rights = new ArrayList<HyperViewItem>(); // right
   private String guid;
   private final String title;
   private Image image;
   private String toolTip;
   private String relationToolTip;
   private String relationLabel;
   private boolean relationDirty = false;
   private boolean highlight = false;
   private boolean current = false;
   private boolean show = true;
   private RelationLink link;

   public HyperViewItem(String title) {
      this(title, null);
   }

   public HyperViewItem(String title, Image image) {
      this.title = title;
      this.image = image;
      this.toolTip = title;
   }

   // lastModified
   public void clearAllSearchItemLinks() {
      bottoms.clear();
      tops.clear();
      lefts.clear();
      rights.clear();
   }

   public Image getMarkImage() {
      return null;
   }

   public void addBottom(HyperViewItem si) {
      bottoms.add(si);
   }

   public void addTop(HyperViewItem si) {
      tops.add(si);
   }

   public void removeTop(HyperViewItem si) {
      if (tops.contains(si)) {
         tops.remove(si);
      }
   }

   public String getShortTitle() {
      return title;
   }

   public void addLeft(HyperViewItem si) {
      lefts.add(si);
   }

   public void addRight(HyperViewItem si) {
      rights.add(si);
   }

   /**
    * @return ArrayList of HyperViewItems
    */
   public ArrayList<HyperViewItem> getBottom() {
      return bottoms;
   }

   /**
    * @return ArrayList of HyperViewItems
    */
   public ArrayList<HyperViewItem> getTop() {
      return tops;
   }

   /**
    * @return ArrayList of HyperViewItems
    */
   public ArrayList<HyperViewItem> getLeft() {
      return lefts;
   }

   /**
    * @return ArrayList of HyperViewItems
    */
   public ArrayList<HyperViewItem> getRight() {
      return rights;
   }

   public Image getImage() throws OseeCoreException {
      return image;
   }

   public String getTitle() {
      return title;
   }

   public String getToolTip() {
      return toolTip;
   }

   public void setToolTip(String toolTip) {
      this.toolTip = toolTip;
   }

   public boolean isShow() {
      return show;
   }

   public void setShow(boolean show) {
      this.show = show;
   }

   public void setImage(Image image) {
      this.image = image;
   }

   public boolean isHighlight() {
      return highlight;
   }

   public void setHighlight(boolean highlight) {
      this.highlight = highlight;
   }

   public boolean isCurrent() {
      return current;
   }

   public void setCurrent(boolean current) {
      this.current = current;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public String getGuid() {
      return guid;
   }

   public String getRelationToolTip() {
      return relationToolTip;
   }

   public void setRelationToolTip(String relationToolTip) {
      this.relationToolTip = relationToolTip;
   }

   public String getRelationLabel() {
      return relationLabel;
   }

   public boolean isRelationDirty() {
      return relationDirty;
   }

   public void setRelationLabel(String relationLabel) {
      this.relationLabel = relationLabel;
   }

   public void setRelationDirty(boolean relationDirty) {
      this.relationDirty = relationDirty;
   }

   public RelationLink getLink() {
      return link;
   }

   public void setLink(RelationLink link) {
      this.link = link;
   }
}