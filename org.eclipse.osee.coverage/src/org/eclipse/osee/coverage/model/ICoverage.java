/*
 * Created on Sep 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Donald G. Dunne
 */
public interface ICoverage {

   public String getNotes();

   public String getAssignees() throws OseeCoreException;

   public boolean isAssignable();

   public Result isEditable();

   public String getGuid();

   public String getName();

   public Collection<? extends ICoverage> getChildren();

   public Collection<? extends ICoverage> getChildren(boolean recurse);

   public OseeImage getOseeImage();

   public boolean isCovered();

   public ICoverage getParent();

   public String getFileContents() throws OseeCoreException;

   public String getLocation();

   public String getNamespace();

   public int getCoveragePercent();

   public String getCoveragePercentStr();

   public boolean isFolder();

   public String getOrderNumber();

}
