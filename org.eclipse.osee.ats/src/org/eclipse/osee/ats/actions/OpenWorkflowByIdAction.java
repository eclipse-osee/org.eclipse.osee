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

import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.SmaWorkflowLabelProvider;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.search.MultipleHridSearchItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class OpenWorkflowByIdAction extends Action {

   public OpenWorkflowByIdAction() {
      super("Open Workflow by Id");
      setToolTipText(getText());
   }

   @Override
   public void run() {
      try {
         MultipleHridSearchItem item = new MultipleHridSearchItem("Open Workflow by Id");
         Collection<Artifact> arts = item.performSearchGetResults(true);
         Artifact artifact = null;
         if (arts.size() == 1) {
            artifact = arts.iterator().next();
         } else {
            ListDialog ld = new ListDialog(Display.getCurrent().getActiveShell());
            ld.setContentProvider(new ArrayContentProvider());
            ld.setLabelProvider(new SmaWorkflowLabelProvider());
            ld.setTitle("Select Workflow");
            ld.setMessage("Select Workflow");
            ld.setInput(arts);
            if (ld.open() == 0) {
               artifact = (Artifact) ld.getResult()[0];
            }
         }
         if (artifact instanceof ActionArtifact) {
            AtsUtil.openAtsAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
         } else {
            SMAEditor.editArtifact(artifact);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.TEAM_WORKFLOW);
   }

}
