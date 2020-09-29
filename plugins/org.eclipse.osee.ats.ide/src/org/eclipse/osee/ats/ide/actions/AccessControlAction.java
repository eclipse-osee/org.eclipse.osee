/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class AccessControlAction extends Action {

   private final AbstractWorkflowArtifact sma;
   private PolicyDialog dialog;

   public AccessControlAction(AbstractWorkflowArtifact sma) {
      super("Access Control");
      setToolTipText(getText());
      this.sma = sma;
   }

   public PolicyDialog getDialog() {
      if (dialog == null) {
         dialog = PolicyDialog.createPolicyDialog(Displays.getActiveShell(), sma);
      }
      return dialog;
   }

   @Override
   public void run() {
      getDialog().open();
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.AUTHENTICATED);
   }

}
