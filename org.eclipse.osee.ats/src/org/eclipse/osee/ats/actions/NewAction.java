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
package org.eclipse.osee.ats.actions;

import java.util.Arrays;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.actions.wizard.NewActionWizard;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.config.BulkLoadAtsCache;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class NewAction extends Action {

   private final String actionableItem;
   private String initialDescription;

   public NewAction() {
      this(null);
   }

   public NewAction(String actionableItem) {
      super("Create New Action");
      this.actionableItem = actionableItem;
      setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("newAction.gif"));
      setToolTipText("Create New Action");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.action.Action#run()
    */
   @Override
   public void run() {
      super.run();
      BulkLoadAtsCache.run(true);
      NewActionWizard wizard = new NewActionWizard();
      try {
         if (actionableItem != null) {
            wizard.setInitialAias(ActionableItemArtifact.getActionableItems(Arrays.asList(actionableItem)));
         }
         if (initialDescription != null) {
            wizard.setInitialDescription(initialDescription);
         }
         WizardDialog dialog =
               new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
         dialog.create();
         if (dialog.open() == 0) {
            Result result = wizard.isActionValid();
            if (result.isFalse()) {
               result.popup();
               return;
            }
            NewActionJob job = null;
            job =
                  new NewActionJob(wizard.getTitle(), wizard.getDescription(), wizard.getChangeType(),
                        wizard.getPriority(), wizard.getNeedBy(), wizard.getValidation(), wizard.getUserCommunities(),
                        wizard.getSelectedActionableItemArtifacts(), wizard);
            job.setUser(true);
            job.setPriority(Job.LONG);
            job.schedule();
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /**
    * @return the initialDescription
    */
   public String getInitialDescription() {
      return initialDescription;
   }

   /**
    * @param initialDescription the initialDescription to set
    */
   public void setInitialDescription(String initialDescription) {
      this.initialDescription = initialDescription;
   }

}