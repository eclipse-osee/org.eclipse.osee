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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Changes the descriptor type of an artifact to the provided descriptor.
 * 
 * @author Jeff C. Phillips
 */
public class ChangeArtifactTypeBlam extends AbstractBlam {

   private XListDropViewer artifactListWidget;
   private XBranchSelectWidget branchWidget;

   private static final String description =
      "Start by drag-and-drop or by pasting GUIDs of artifacts. Log what the previous type of each artifact was because that information is loss after running this blam";

   public ChangeArtifactTypeBlam() {
      super(null, description, BlamUiSource.FILE);
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ChangeArtifactType.changeArtifactType(variableMap.getArtifacts("artifacts"),
         variableMap.getArtifactType("New Artifact Type"));
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      String widgetLabel = xWidget.getLabel();

      if (widgetLabel.equals("artifacts")) {
         artifactListWidget = (XListDropViewer) xWidget;
      } else if (widgetLabel.equals("Branch")) {
         branchWidget = (XBranchSelectWidget) xWidget;
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      String widgetName = xWidget.getLabel();
      if (widgetName.equals("artifacts")) {
         final Menu popupMenu = artifactListWidget.popupMenu();
         MenuItem paste = new MenuItem(popupMenu, SWT.NONE);
         paste.setText("Paste GUIDs from Clipboard");
         paste.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     final Clipboard cb = new Clipboard(popupMenu.getDisplay());
                     TextTransfer transfer = TextTransfer.getInstance();
                     String data = (String) cb.getContents(transfer);

                     String[] guids = data.split("\t|\n|\r");
                     for (String guid : guids) {
                        if (GUID.isValid(guid)) {
                           try {
                              Artifact artifact = ArtifactQuery.getArtifactFromId(guid, branchWidget.getSelection());
                              artifactListWidget.addToInput(artifact);
                           } catch (Exception ex) {
                              OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                           }
                        }
                     }
                  }
               });
            }

         });

      }
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}