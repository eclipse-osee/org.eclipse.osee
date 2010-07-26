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
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class DirtyReportAction extends Action {

   private final StateMachineArtifact sma;

   public DirtyReportAction(StateMachineArtifact sma) {
      super("Show Artifact Dirty Report");
      this.sma = sma;
      setToolTipText("Show what attribute or relation making editor dirty.");
   }

   @Override
   public void run() {
      Result result = sma.getEditor().isDirtyResult();
      AWorkbench.popup("Dirty Report", result.isFalse() ? "Not Dirty" : "Dirty -> " + result.getText());
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DIRTY);
   }

}
