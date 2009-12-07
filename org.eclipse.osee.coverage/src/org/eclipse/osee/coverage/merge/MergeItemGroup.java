/*
 * Created on Nov 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.merge;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoverageMetrics;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Donald G. Dunne
 */
public class MergeItemGroup implements IMergeItem {

   private final MergeType mergeType;
   private boolean checked = false;
   private boolean importAllowed = true;
   private final Collection<IMergeItem> mergeItems;

   public MergeItemGroup(MergeType mergeType, Collection<IMergeItem> mergeItems) {
      this.mergeType = mergeType;
      this.mergeItems = mergeItems;
   }

   public MergeType getMergeType() {
      return mergeType;
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
      return mergeType.toString() + " - " + mergeItems.toString();
   }

   @Override
   public String getAssignees() throws OseeCoreException {
      return "";
   }

   @Override
   public Collection<? extends ICoverage> getChildren() {
      return mergeItems;
   }

   @Override
   public Collection<? extends ICoverage> getChildren(boolean recurse) {
      Set<ICoverage> children = new HashSet<ICoverage>();
      for (IMergeItem mergeItem : mergeItems) {
         if (mergeItem instanceof MergeItem) {
            children.addAll(((MergeItem) mergeItem).getImportItem().getChildren(recurse));
         }
      }
      return children;
   }

   @Override
   public int getCoveragePercent() {
      Collection<? extends ICoverage> children = getChildren(true);
      Set<ICoverage> covered = new HashSet<ICoverage>();
      for (ICoverage coverage : children) {
         if (coverage.isCovered()) covered.add(coverage);
      }
      return CoverageMetrics.getPercent(covered.size(), children.size()).getFirst();
   }

   @Override
   public String getCoveragePercentStr() {
      Collection<? extends ICoverage> children = getChildren(true);
      Set<ICoverage> covered = new HashSet<ICoverage>();
      for (ICoverage coverage : children) {
         if (coverage.isCovered()) covered.add(coverage);
      }
      return CoverageMetrics.getPercent(covered.size(), children.size()).getSecond();
   }

   @Override
   public String getGuid() {
      return "";
   }

   @Override
   public String getLocation() {
      return "";
   }

   @Override
   public String getName() {
      return mergeType.toString();
   }

   @Override
   public String getNamespace() {
      return mergeItems.iterator().next().getNamespace();
   }

   @Override
   public String getNotes() {
      return "";
   }

   @Override
   public OseeImage getOseeImage() {
      return mergeItems.iterator().next().getOseeImage();
   }

   @Override
   public ICoverage getParent() {
      return mergeItems.iterator().next().getParent();
   }

   @Override
   public String getFileContents() {
      return mergeItems.iterator().next().getFileContents();
   }

   @Override
   public boolean isAssignable() {
      return false;
   }

   @Override
   public boolean isCovered() {
      return false;
   }

   @Override
   public Result isEditable() {
      return null;
   }

   @Override
   public boolean isFolder() {
      return false;
   }

   public boolean isChecked() {
      return checked;
   }

   public Collection<IMergeItem> getMergeItems() {
      return mergeItems;
   }

   @Override
   public String getOrderNumber() {
      return "";
   }

}
