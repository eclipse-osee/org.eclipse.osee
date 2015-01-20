/*
 * Created on Jan 20, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.agile;

import java.util.Collection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;

public class SprintFilteredListDialog extends FilteredTreeDialog {

   public SprintFilteredListDialog(String dialogTitle, String dialogMessage, Collection<? extends IAgileSprint> values) {
      super(dialogTitle, dialogMessage, new ArrayContentProvider(), new StringLabelProvider());
   }

}
