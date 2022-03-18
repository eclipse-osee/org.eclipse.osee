/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.reqif.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.icteam.reqif.dialogs.ExportReqIfDialog;
import org.eclipse.osee.icteam.reqif.export.ReqIfCreateResoure;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler to handle the Export of Reqif Requirements
 * 
 * @author Manjunath Sangappa
 */
public class ExportReqIfHandler extends AbstractHandler {


  @Override
  public Object execute(final ExecutionEvent event) {

    ISelection currentSelection = HandlerUtil.getCurrentSelection(event);

    if (currentSelection instanceof IStructuredSelection) {
      try {
        IStructuredSelection structSel = (IStructuredSelection) currentSelection;

        if (!structSel.isEmpty()) {
          Object firstElement = structSel.getFirstElement();
          if (firstElement instanceof Artifact) {

            Artifact parentArtifact = (Artifact) firstElement;
            List<Artifact> requirmentChildren = getRequirmentChildren(parentArtifact);

            final ExportReqIfDialog dialog = new ExportReqIfDialog(Displays.getActiveShell());
            int open = dialog.open();
            if (open == 0) {
              ReqIfCreateResoure createResource = new ReqIfCreateResoure();
              createResource.createReqIfResource(dialog.getDirName() + "/" + dialog.getFileName(), requirmentChildren);
            }
          }
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }

    }
    return null;
  }

  /**
   * Get all the Requirement Folders from the selected WorkSpace
   * 
   * @param parentArtifact : Project module (Workspace of Project)
   * @return : list of Requirement folders
   * @throws OseeCoreException :
   */
  public List<Artifact> getRequirmentChildren(final Artifact parentArtifact) throws OseeCoreException {
    List<Artifact> artifactList = new ArrayList<Artifact>();
    List<Artifact> children = parentArtifact.getChildren();
    for (Artifact artifact : children) {
      if (artifact.getArtifactType().getName().equalsIgnoreCase(AtsArtifactTypes.RequirementDocument.getName())) {
        List<Artifact> children2 = artifact.getChildren();
        for (Artifact artifact2 : children2) {
          if (artifact2.getArtifactType().getName()
              .equalsIgnoreCase(AtsArtifactTypes.Software_ReQ.getName())) {
            artifactList.add(artifact2);
          }
        }
      }
    }
    return artifactList;
  }

}