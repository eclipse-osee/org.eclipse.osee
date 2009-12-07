/*
 * Created on Dec 2, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.merge;

import org.eclipse.osee.coverage.model.ICoverage;

/**
 * @author Donald G. Dunne
 */
public interface IMergeItem extends ICoverage {

   public MergeType getMergeType();

   public boolean isChecked();

}
