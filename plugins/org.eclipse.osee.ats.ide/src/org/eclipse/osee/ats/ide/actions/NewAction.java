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
package org.eclipse.osee.ats.ide.actions;

import java.util.Arrays;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.actions.wizard.NewActionWizard;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class NewAction extends AbstractAtsAction {

   private final String actionableItem;

   public NewAction() {
      this(null);
   }

   public NewAction(String actionableItem) {
      super("Create New Action");
      this.actionableItem = actionableItem;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.NEW_ACTION));
      setToolTipText("Create New Action");
   }

   @Override
   public void runWithException() {
      NewActionWizard wizard = new NewActionWizard();
      if (actionableItem != null) {
         wizard.setInitialAias(AtsClientService.get().getActionableItemService().getActionableItems(
            Arrays.asList(actionableItem)));
      }
      WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
      dialog.create();
      dialog.open();
   }

}