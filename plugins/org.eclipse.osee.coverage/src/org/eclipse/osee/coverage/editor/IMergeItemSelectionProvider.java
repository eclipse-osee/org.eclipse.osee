/*
 * Created on Apr 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor;

import java.util.Collection;
import org.eclipse.osee.coverage.merge.IMergeItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IMergeItemSelectionProvider {

   public Collection<IMergeItem> getSelectedMergeItems() throws OseeCoreException;
}
