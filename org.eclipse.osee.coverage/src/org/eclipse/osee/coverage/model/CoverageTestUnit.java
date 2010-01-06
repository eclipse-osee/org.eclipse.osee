/*
 * Created on Sep 17, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.Collection;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * Single test that can cover multiple Coverage Items
 * 
 * @author Donald G. Dunne
 */
public class CoverageTestUnit implements ICoverage {

   String name;
   String guid = GUID.create();

   public CoverageTestUnit(String name) {
      super();
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getGuid() {
      return guid;
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public String getAssignees() throws OseeCoreException {
      return "";
   }

   @Override
   public Result isEditable() {
      return Result.FalseResult;
   }

   @Override
   public OseeImage getOseeImage() {
      if (isCovered()) {
         return CoverageImage.TEST_UNIT_GREEN;
      }
      return CoverageImage.TEST_UNIT_RED;
   }

   @Override
   public boolean isCovered() {
      return false;
   }

   @Override
   public ICoverage getParent() {
      return null;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   @Override
   public boolean isAssignable() {
      return false;
   }

   @Override
   public String getNotes() {
      return null;
   }

   @Override
   public int getCoveragePercent() {
      return 0;
   }

   @Override
   public Collection<? extends ICoverage> getChildren(boolean recurse) {
      return java.util.Collections.emptyList();
   }

   @Override
   public String getCoveragePercentStr() {
      return "";
   }

   @Override
   public boolean isFolder() {
      return false;
   }

   @Override
   public Collection<? extends ICoverage> getChildren() {
      return getChildren(false);
   }

   @Override
   public String getLocation() {
      return "";
   }

   @Override
   public String getNamespace() {
      return "";
   }

   @Override
   public String getFileContents() throws OseeCoreException {
      return "";
   }

   @Override
   public String getOrderNumber() {
      return "";
   }

}
