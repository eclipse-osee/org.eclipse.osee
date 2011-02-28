/*
 * Created on Feb 25, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

public class OpenPerspectiveNavigateItem extends XNavigateItem {

   private final String perspectiveId;

   public OpenPerspectiveNavigateItem(XNavigateItem parent, String perspectiveName, String perspectiveId, KeyedImage oseeImage) {
      super(parent, "Open " + perspectiveName + " Perspective", oseeImage);
      this.perspectiveId = perspectiveId;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      AWorkbench.openPerspective(perspectiveId);
   }

}
