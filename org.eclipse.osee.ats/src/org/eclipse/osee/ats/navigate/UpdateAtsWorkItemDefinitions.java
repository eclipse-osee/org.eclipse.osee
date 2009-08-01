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
package org.eclipse.osee.ats.navigate;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.config.AtsDatabaseConfig;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class UpdateAtsWorkItemDefinitions extends XNavigateItemAction {

   /**
    * @param parent
    */
   public UpdateAtsWorkItemDefinitions(XNavigateItem parent) {
      super(parent, "Update Ats WorkItemDefinitions", FrameworkImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(),
            "This could break lots of things, are you SURE?")) return;

      XResultData xResultData = new XResultData();
      AtsDatabaseConfig.configWorkItemDefinitions(WriteType.Update, xResultData);
      if (xResultData.isEmpty()) {
         xResultData.log("Nothing updated");
      }
      xResultData.report(getName());

      AWorkbench.popup("Completed", getName());
   }

}
