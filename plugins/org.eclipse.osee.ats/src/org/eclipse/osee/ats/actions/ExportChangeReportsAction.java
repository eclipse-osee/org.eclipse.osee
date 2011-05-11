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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.operation.ExportChangeReportOperation;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ExportChangeReportsAction extends Action {
   private final WorldEditor worldEditor;

   public ExportChangeReportsAction(WorldEditor worldEditor) {
      super();
      setText("Export Change Report(s)");
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.EXPORT_DATA));
      this.worldEditor = worldEditor;
   }

   public List<TeamWorkFlowArtifact> getWorkflows() {
      ArrayList<TeamWorkFlowArtifact> workflows = new ArrayList<TeamWorkFlowArtifact>();
      workflows.addAll(worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts());
      return workflows;
   }

   @Override
   public void run() {
      IOperation operation = new ExportChangeReportOperation(getWorkflows(), false);
      Operations.executeAsJob(operation, true);
   }

   public void updateEnablement() {
      setEnabled(!getWorkflows().isEmpty());
   }
}