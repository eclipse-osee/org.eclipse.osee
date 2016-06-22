/*******************************************************************************
 * Copyright (c) 2016 Boeing.
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
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class OpenInBrowserAction extends Action {

   private final AbstractWorkflowArtifact awa;

   public OpenInBrowserAction(AbstractWorkflowArtifact awa) {
      super();
      this.awa = awa;
      setText("Open " + awa.getArtifactTypeName() + " in Browser");
      setToolTipText(getText());
   }

   @Override
   public void run() {
      Program.launch(AtsUtilCore.getActionUrl(awa.getAtsId(), AtsClientService.get()));
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getProgramImageDescriptor("html");
   }

}
