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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;

/**
 * @author Ryan D. Brooks
 */
public class BlamEditor extends AbstractArtifactEditor implements IBlamEventListener {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.blam.BlamEditor";
   private OverviewPage overviewPage;
   private List<XWidget> widgets;
   private VariableMap blamVariableMap;

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

   public void appendOuputLine(final String additionalOutput) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            overviewPage.appendOuputLine(additionalOutput);
         }
      });
   }

   public void setOuputText(final String text) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            overviewPage.setOuputText(text);
         }
      });
   }

   public BlamWorkflow getWorkflow() {
      return (BlamWorkflow) ((BlamEditorInput) getEditorInput()).getArtifact();
   }

   public static void edit(BlamWorkflow blamWorkflow) {
      BlamEditor.edit(new BlamEditorInput(blamWorkflow));
   }

   public static void edit(final BlamEditorInput blamEditorInput) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               AWorkbench.getActivePage().openEditor(blamEditorInput, EDITOR_ID);
            } catch (PartInitException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      });
   }

   public static void edit(String workflowId) throws OseeCoreException {
      BlamEditor.edit(new BlamEditorInput(workflowId));
   }

   public static void edit(BlamOperation blamOperation) throws OseeCoreException {
      BlamEditor.edit(new BlamEditorInput(blamOperation));
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
         setOuputText("Starting BLAM at " + blamStartEvent.getDate() + "\n");

      } else if (blamEvent instanceof BlamFinishedEvent) {
         BlamFinishedEvent blamFinishedEvent = (BlamFinishedEvent) blamEvent;
         appendOuputLine("BLAM completed in " + (blamFinishedEvent.getDurationMillis() / 1000) + " secs");
      }
   }

   @Override
   protected void setInput(IEditorInput input) {
      super.setInput(input);

      blamVariableMap = new VariableMap();
   }

   /**
    * @return the blamVariableMap
    */
   protected VariableMap getBlamVariableMap() {
      return blamVariableMap;
   }
}