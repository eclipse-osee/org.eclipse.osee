/*********************************************************************
 * Copyright (c) 2016 Boeing
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
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
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
      Program.launch(AtsUtil.getActionUrl(awa.getAtsId(), AtsApiService.get()));
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getProgramImageDescriptor("html");
   }

}
