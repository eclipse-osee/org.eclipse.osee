/*
 * Created on Nov 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.merge;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class MergeItem extends MergeItemBase {

   private final ICoverage packageItem;
   private final ICoverage importItem;

   public MergeItem(MergeType mergeType, ICoverage packageItem, ICoverage importItem, boolean isCheckable) {
      super(mergeType, isCheckable);
      this.packageItem = packageItem;
      this.importItem = importItem;
   }

   public ICoverage getPackageItem() {
      return packageItem;
   }

   public ICoverage getImportItem() {
      return importItem;
   }

   @Override
   public String getAssignees() throws OseeCoreException {
      if (importItem != null) return importItem.getAssignees();
      return "";
   }

   @Override
   public Collection<? extends ICoverage> getChildren() {
      if (importItem != null) return importItem.getChildren();
      return Collections.emptyList();
   }

   @Override
   public Collection<? extends ICoverage> getChildren(boolean recurse) {
      if (importItem != null) return importItem.getChildren(recurse);
      return Collections.emptyList();
   }

   @Override
   public int getCoveragePercent() {
      if (importItem != null) return importItem.getCoveragePercent();
      return 0;
   }

   @Override
   public String getCoveragePercentStr() {
      if (importItem != null) return importItem.getCoveragePercentStr();
      return "0";
   }

   @Override
   public String getGuid() {
      if (importItem != null) return importItem.getGuid();
      return "";
   }

   @Override
   public String getLocation() {
      if (importItem != null) return importItem.getLocation();
      return packageItem.getLocation();
   }

   @Override
   public String getName() {
      if (importItem != null)
         return importItem.getName();
      else
         return packageItem.getName();
   }

   @Override
   public String getNamespace() {
      if (importItem != null) return importItem.getNamespace();
      return packageItem.getNamespace();
   }

   @Override
   public String getNotes() {
      if (importItem != null) return importItem.getNotes();
      return packageItem.getNotes();
   }

   @Override
   public KeyedImage getOseeImage() {
      if (importItem != null) return importItem.getOseeImage();
      return packageItem.getOseeImage();
   }

   @Override
   public ICoverage getParent() {
      if (importItem != null) return importItem.getParent();
      return packageItem.getParent();
   }

   @Override
   public String getFileContents() throws OseeCoreException {
      if (importItem != null) return importItem.getFileContents();
      return packageItem.getFileContents();
   }

   @Override
   public boolean isAssignable() {
      if (importItem != null) return importItem.isAssignable();
      return packageItem.isAssignable();
   }

   @Override
   public boolean isCovered() {
      if (importItem != null) return importItem.isCovered();
      return packageItem.isCovered();
   }

   @Override
   public Result isEditable() {
      if (importItem != null) return importItem.isEditable();
      return packageItem.isEditable();
   }

   @Override
   public boolean isFolder() {
      if (importItem != null) return importItem.isFolder();
      return packageItem.isFolder();
   }

   @Override
   public String toString() {
      if (importItem != null) return getMergeType().toString() + " - " + importItem.toString();
      return getMergeType().toString() + " - " + packageItem.toString();
   }

   @Override
   public String getOrderNumber() {
      return importItem.getOrderNumber();
   }

}
