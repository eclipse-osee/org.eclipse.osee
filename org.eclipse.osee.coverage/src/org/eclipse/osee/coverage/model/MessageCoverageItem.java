/*
 * Created on Nov 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Donald G. Dunne
 */
public class MessageCoverageItem implements ICoverage {

   private final String message;

   public MessageCoverageItem(String message) {
      this.message = message;
   }

   @Override
   public String getName() {
      return message;
   }

   @Override
   public Collection<? extends ICoverage> getChildren() {
      return Collections.emptyList();
   }

   @Override
   public Collection<? extends ICoverage> getChildren(boolean recurse) {
      return Collections.emptyList();
   }

   @Override
   public String getAssignees() throws OseeCoreException {
      return "";
   }

   @Override
   public int getCoveragePercent() {
      return 0;
   }

   @Override
   public String getCoveragePercentStr() {
      return "";
   }

   @Override
   public String getFileContents() throws OseeCoreException {
      return null;
   }

   @Override
   public String getGuid() {
      return null;
   }

   @Override
   public String getLocation() {
      return null;
   }

   @Override
   public String getNamespace() {
      return null;
   }

   @Override
   public String getNotes() {
      return null;
   }

   @Override
   public OseeImage getOseeImage() {
      return null;
   }

   @Override
   public ICoverage getParent() {
      return null;
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

   @Override
   public String getOrderNumber() {
      return "";
   }

}
