/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import java.sql.SQLException;
import java.util.Arrays;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItem extends XNavigateItemAction {

   /**
    * @param parent
    */
   public DoesNotWorkItem(XNavigateItem parent) {
      super(parent, "Does Not Work - Delete null attributes");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;

      deleteNullAttributes();
      //      XNavigateItem item = AtsNavigateViewItems.getInstance().getSearchNavigateItems().get(1);
      //      System.out.println("Item " + item.getName());
      //      NavigateView.getNavigateView().handleDoubleClick(item);

      //      XResultData.runExample();

      // fixOseePeerReviews();

      AWorkbench.popup("Completed", "Complete");
   }

   private void deleteNullAttributes() throws CoreException, SQLException {
      int x = 0;
      for (String artTypeName : Arrays.asList("Lba V13 Code Team Workflow", "Lba V13 Test Team Workflow",
            "Lba V13 Req Team Workflow", "Lba V13 SW Design Team Workflow", "Lba V13 Tech Approach Team Workflow",
            "Lba V11 REU Code Team Workflow", "Lba V11 REU Test Team Workflow", "Lba V11 REU Req Team Workflow",
            "Lba B3 Code Team Workflow", "Lba B3 Test Team Workflow", "Lba B3 Req Team Workflow",
            "Lba B3 SW Design Team Workflow", "Lba B3 Tech Approach Team Workflow")) {
         for (Artifact team : ArtifactQuery.getArtifactsFromType(artTypeName, AtsPlugin.getAtsBranch())) {
            for (Attribute<?> attr : team.getAttributes()) {
               if (attr.getValue() == null) {
                  System.out.println(team.getHumanReadableId() + " - " + attr.getNameValueDescription());
                  //                  attr.delete();
                  x++;
               }
            }
            team.persistAttributes();
         }
      }
      System.out.println("Deleted " + x);
   }

}
