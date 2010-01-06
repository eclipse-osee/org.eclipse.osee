/*
 * Created on Nov 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.merge;

import java.util.Collection;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

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
      return importItem.getAssignees();
   }

   @Override
   public Collection<? extends ICoverage> getChildren() {
      return importItem.getChildren();
   }

   @Override
   public Collection<? extends ICoverage> getChildren(boolean recurse) {
      return importItem.getChildren(recurse);
   }

   @Override
   public int getCoveragePercent() {
      return importItem.getCoveragePercent();
   }

   @Override
   public String getCoveragePercentStr() {
      return importItem.getCoveragePercentStr();
   }

   @Override
   public String getGuid() {
      return importItem.getGuid();
   }

   @Override
   public String getLocation() {
      return importItem.getLocation();
   }

   @Override
   public String getName() {
      return importItem.getName();
   }

   @Override
   public String getNamespace() {
      return importItem.getNamespace();
   }

   @Override
   public String getNotes() {
      return importItem.getNotes();
   }

   @Override
   public OseeImage getOseeImage() {
      return importItem.getOseeImage();
   }

   @Override
   public ICoverage getParent() {
      return importItem.getParent();
   }

   @Override
   public String getFileContents() throws OseeCoreException {
      return importItem.getFileContents();
   }

   @Override
   public boolean isAssignable() {
      return importItem.isAssignable();
   }

   @Override
   public boolean isCovered() {
      return importItem.isCovered();
   }

   @Override
   public Result isEditable() {
      return importItem.isEditable();
   }

   @Override
   public boolean isFolder() {
      return importItem.isFolder();
   }

   @Override
   public String toString() {
      return getMergeType().toString() + " - " + importItem.toString();
   }

   @Override
   public String getOrderNumber() {
      return importItem.getOrderNumber();
   }

}
