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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
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
         dialog = new PolicyDialog(Displays.getActiveShell(), sma);
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
