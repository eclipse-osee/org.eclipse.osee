/*
 * Created on Dec 2, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.merge;

import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public interface IMergeItem extends ICoverage {

   public MergeType getMergeType();

   public boolean isChecked();

   public boolean isCheckable();

   public void setChecked(boolean checked) throws OseeArgumentException;

   public boolean isImportAllowed();

}
