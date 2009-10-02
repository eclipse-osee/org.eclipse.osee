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
package org.eclipse.osee.coverage.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewerFactory;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * Single code unit (file/procedure/function) that can contain other Coverage Unit or Coverage Items
 * 
 * @author Donald G. Dunne
 */
public class CoverageUnit implements ICoverageEditorItem {

   private String name;
   private final String guid = GUID.create();
   private String text;
   private final List<CoverageItem> coverageItems = new ArrayList<CoverageItem>();
   private String location;
   private final List<CoverageUnit> coverageUnits = new ArrayList<CoverageUnit>();
   private final CoverageUnit parentCoverageUnit;

   public CoverageUnit(CoverageUnit parentCoverageUnit, String name, String location) {
      super();
      this.parentCoverageUnit = parentCoverageUnit;
      this.name = name;
      this.location = location;
   }

   public void addCoverageUnit(CoverageUnit coverageUnit) {
      coverageUnits.add(coverageUnit);
   }

   public List<CoverageUnit> getCoverageUnits() {
      return coverageUnits;
   }

   public void addCoverageItem(CoverageItem coverageItem) {
      coverageItems.add(coverageItem);
   }

   public List<CoverageItem> getCoverageItems(boolean recurse) {
      if (!recurse) {
         return coverageItems;
      }
      List<CoverageItem> items = new ArrayList<CoverageItem>(coverageItems);
      for (CoverageUnit coverageUnit : coverageUnits) {
         items.addAll(coverageUnit.getCoverageItems(true));
      }
      return items;
   }

   public CoverageItem getCoverageItem(String methodNum, String executionLine) {
      for (CoverageItem coverageItem : getCoverageItems(true)) {
         if (coverageItem.getMethodNum().equals(methodNum) && coverageItem.getExecuteNum().equals(executionLine)) {
            return coverageItem;
         }
      }
      return null;
   }

   public CoverageUnit getCoverageUnit(String index) {
      return coverageUnits.get(new Integer(index).intValue() - 1);
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public String getGuid() {
      return guid;
   }

   public CoverageUnit getParentCoverageUnit() {
      return parentCoverageUnit;
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public User getUser() {
      return null;
   }

   @Override
   public Result isEditable() {
      return null;
   }

   @Override
   public void setUser(User user) {
   }

   @Override
   public boolean isCompleted() {
      for (CoverageItem coverageItem : getCoverageItems(true)) {
         if (!coverageItem.isCompleted()) return false;
      }
      return true;
   }

   @Override
   public String getCoverageEditorValue(XViewerColumn xCol) {
      if (xCol.equals(CoverageXViewerFactory.Parent_Coverage_Unit)) return getParentCoverageUnit() == null ? "" : getParentCoverageUnit().getName();
      return "";
   }

   @Override
   public Object[] getChildren() {
      List<ICoverageEditorItem> children = new ArrayList<ICoverageEditorItem>();
      children.addAll(getCoverageUnits());
      children.addAll(getCoverageItems(false));
      return children.toArray(new Object[children.size()]);
   }

   @Override
   public OseeImage getOseeImage() {
      if (isCovered()) {
         return CoverageImage.UNIT_GREEN;
      }
      return CoverageImage.UNIT_RED;
   }

   @Override
   public boolean isCovered() {
      for (CoverageItem coverageItem : getCoverageItems(true)) {
         if (!coverageItem.isCovered()) return false;
      }
      return true;
   }

}
