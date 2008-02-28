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
package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;

/**
 * @author Ryan D. Brooks
 */
public class WorkflowEditor extends AbstractArtifactEditor implements IBlamEventListener {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.blam.WorkflowEditor";
   private OverviewPage overviewPage;
   private List<XWidget> widgets;
   private BlamVariableMap blamVariableMap;

   @Override
   protected void addPages() {
      try {
         overviewPage = new OverviewPage(this);
         addPage(overviewPage);
         addPage(new WorkflowDataPage(this, overviewPage));
         setPartName("BLAM: " + getWorkflow().getDescriptiveName());
         setTitleImage(SkynetGuiPlugin.getInstance().getImage("blam.gif"));
      } catch (PartInitException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   public void appendOuputText(String additionalOutput) {
      overviewPage.appendOuputText(additionalOutput);
   }

   private void setOutputText(final String output) {
      Displays.ensureInDisplayThread(new Runnable() {

         public void run() {
            overviewPage.setOuputText(output);
         }

      });
   }

   public BlamWorkflow getWorkflow() {
      return (BlamWorkflow) getEditorInput().getArtifact();
   }

   /**
    * artifact must be of type Workflow. The paramter type is artifact to allow this method to have the same signature
    * as the corresponding method in ArtifactExloper in the hope on day this will use a common interface
    * 
    * @param artifact
    */
   public static void editArtifact(final Artifact artifact) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), EDITOR_ID);
            } catch (PartInitException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      });
   }

   /**
    * @return the widgets
    */
   public List<XWidget> getWidgets() {
      return widgets;
   }

   public void onEvent(IBlamEvent blamEvent) {

      if (blamEvent instanceof BlamStartedEvent) {
         BlamStartedEvent blamStartEvent = (BlamStartedEvent) blamEvent;
         setOutputText("Starting workflow at " + blamStartEvent.getDate());

      } else if (blamEvent instanceof BlamFinishedEvent) {
         BlamFinishedEvent blamFinishedEvent = (BlamFinishedEvent) blamEvent;
         setOutputText("Workflow completed in " + (blamFinishedEvent.getDurationMillis() / 1000) + " secs");
      }
   }

   @Override
   protected void setInput(IEditorInput input) {
      super.setInput(input);

      blamVariableMap = new BlamVariableMap();
   }

   /**
    * @return the blamVariableMap
    */
   protected BlamVariableMap getBlamVariableMap() {
      return blamVariableMap;
   }
}