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
package org.eclipse.osee.framework.ui.skynet.artifact;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceFileArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceURL;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Jeff C. Phillips
 */
public class OpenArtifactAction implements IObjectActionDelegate {
   private IWorkbenchPart targetPart;
   private Shell shell;

   public OpenArtifactAction() {
      super();
   }

   public void setActivePart(IAction action, IWorkbenchPart targetPart) {
      this.targetPart = targetPart;
      shell = targetPart.getSite().getShell();
   }

   public void run(IAction action) {
      Object object = (Object) AWorkspace.getSelection(targetPart).getFirstElement();
      Artifact artifact = null;
      IFile iFile = null;

      if (object instanceof IFile) {
         iFile = (IFile) object;
         try {
            artifact = WorkspaceFileArtifact.getArtifactFromWorkspaceFile(WorkspaceURL.getURL(iFile), shell);
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }

         if (artifact != null) {
            ArtifactEditor.editArtifact(artifact);
         }
      }
   }

   public void selectionChanged(IAction action, ISelection selection) {
   }
}
