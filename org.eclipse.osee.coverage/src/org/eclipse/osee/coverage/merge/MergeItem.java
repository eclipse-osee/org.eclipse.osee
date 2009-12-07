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
public class MergeItem implements IMergeItem {

   private final MergeType mergeType;
   private final ICoverage packageItem;
   private final ICoverage importItem;
   private boolean checked = false;
   private boolean importAllowed = true;

   public MergeItem(MergeType mergeType, ICoverage packageItem, ICoverage importItem) {
      this.mergeType = mergeType;
      this.packageItem = packageItem;
      this.importItem = importItem;
   }

   public MergeType getMergeType() {
      return mergeType;
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
   public String getFileContents() {
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

   public boolean isChecked() {
      return checked;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   public boolean isImportAllowed() {
      return importAllowed;
   }

   public void setImportAllowed(boolean importAllowed) {
      this.importAllowed = importAllowed;
   }

   @Override
   public String toString() {
      return mergeType.toString() + " - " + importItem.toString();
   }

   @Override
   public String getOrderNumber() {
      return importItem.getOrderNumber();
   }

}
