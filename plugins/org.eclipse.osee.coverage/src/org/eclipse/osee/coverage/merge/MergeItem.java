/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.merge;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class MergeItem extends MergeItemBase {

   private final ICoverage packageItem;
   private final ICoverage importItem;

   public MergeItem(MergeType mergeType, ICoverage packageItem, ICoverage importItem, boolean isCheckable) {
      super(importItem == null ? "" : importItem.getGuid(), importItem == null ? String.format("Package [%s]",
         packageItem.getName()) : importItem.getName(), mergeType, isCheckable);
      this.packageItem = packageItem;
      this.importItem = importItem;
   }

   public MergeItem(MergeType mergeType, String name) {
      super("", name, mergeType, false);
      this.packageItem = null;
      this.importItem = null;
   }

   public ICoverage getPackageItem() {
      return packageItem;
   }

   public ICoverage getImportItem() {
      return importItem;
   }

   @Override
   public String getAssignees() throws OseeCoreException {
      if (importItem != null) {
         return importItem.getAssignees();
      }
      return "";
   }

   @Override
   public Collection<? extends ICoverage> getChildren() {
      if (importItem != null) {
         return importItem.getChildren();
      }
      return Collections.emptyList();
   }

   @Override
   public Collection<? extends ICoverage> getChildren(boolean recurse) {
      if (importItem != null) {
         return importItem.getChildren(recurse);
      }
      return Collections.emptyList();
   }

   @Override
   public Double getCoveragePercent() {
      if (importItem != null) {
         return importItem.getCoveragePercent();
      }
      return 0.0;
   }

   @Override
   public String getCoveragePercentStr() {
      if (importItem != null) {
         return importItem.getCoveragePercentStr();
      }
      return "0";
   }

   @Override
   public String getLocation() {
      if (importItem != null) {
         return importItem.getLocation();
      }
      return packageItem.getLocation();
   }

   @Override
   public String getNamespace() {
      if (importItem != null) {
         return importItem.getNamespace();
      }
      return packageItem.getNamespace();
   }

   @Override
   public String getNotes() {
      if (importItem != null) {
         return importItem.getNotes();
      }
      return String.format("Package [%s]", packageItem.getNotes());
   }

   @Override
   public KeyedImage getOseeImage() {
      if (importItem != null) {
         return importItem.getOseeImage();
      }
      return packageItem.getOseeImage();
   }

   @Override
   public ICoverage getParent() {
      if (importItem != null) {
         return importItem.getParent();
      }
      return packageItem.getParent();
   }

   @Override
   public String getFileContents() throws OseeCoreException {
      if (importItem != null) {
         return importItem.getFileContents();
      }
      return packageItem.getFileContents();
   }

   @Override
   public boolean isAssignable() {
      if (importItem != null) {
         return importItem.isAssignable();
      }
      return packageItem.isAssignable();
   }

   @Override
   public boolean isCovered() {
      if (importItem != null) {
         return importItem.isCovered();
      }
      return packageItem.isCovered();
   }

   @Override
   public Result isEditable() {
      if (importItem != null) {
         return importItem.isEditable();
      }
      return packageItem.isEditable();
   }

   @Override
   public boolean isFolder() {
      if (importItem != null) {
         return importItem.isFolder();
      }
      return packageItem.isFolder();
   }

   @Override
   public String toString() {
      if (importItem != null) {
         return getMergeType().toString() + " - " + importItem.toString();
      }
      return getMergeType().toString() + " - " + packageItem.toString();
   }

   @Override
   public String getOrderNumber() {
      if (Strings.isValid(importItem.getOrderNumber())) {
         return importItem.getOrderNumber();
      } else if (Strings.isValid(packageItem.getOrderNumber())) {
         return packageItem.getOrderNumber();
      }
      return "";
   }

}
