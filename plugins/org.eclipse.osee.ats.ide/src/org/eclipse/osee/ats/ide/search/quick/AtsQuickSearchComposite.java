/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.search.quick;

import java.io.File;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class AtsQuickSearchComposite extends Composite {

   Text searchArea;
   XCheckBox completeCancelledCheck;

   public AtsQuickSearchComposite(Composite parent, int style) {
      super(parent, style);
      setLayout(ALayout.getZeroMarginLayout(4, false));
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Button searchButton = new Button(this, SWT.PUSH);
      searchButton.setText("Search:");
      searchButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleSearch();
         }
      });
      searchButton.addMouseListener(new MouseListener() {

         @Override
         public void mouseUp(MouseEvent mouseEvent) {
            // do nothing
         }

         @Override
         public void mouseDoubleClick(MouseEvent mouseEvent) {
            if (mouseEvent.button == 3) {
               try {
                  File file = OseeInf.getResourceAsFile("misc/OSEEDay.wav", getClass());
                  Program.launch(file.getAbsolutePath());
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }

         @Override
         public void mouseDown(MouseEvent arg0) {
            // do nothing
         }
      });

      GridData gridData = new GridData(SWT.RIGHT, SWT.NONE, false, false);
      gridData.heightHint = 15;
      searchArea = new Text(this, SWT.SINGLE | SWT.BORDER);
      GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
      searchArea.setFont(parent.getFont());
      searchArea.setLayoutData(gd);
      searchArea.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent event) {
            if (event.character == '\r') {
               handleSearch();
            }
         }
      });

      searchArea.setToolTipText(
         "ATS Quick Search - Type in a search string and press enter.\nOr right-click Paste and Go.");
      addContextMenu(searchArea);

      completeCancelledCheck = new XCheckBox("IC");
      completeCancelledCheck.createWidgets(this, 2);
      completeCancelledCheck.setToolTip("Include completed/cancelled ATS Artifacts");

   }

   /**
    * Since adding new menu replaces the default menu, we must re-create the default copy/paste options
    */
   private void addContextMenu(final Text control) {
      Menu menu = new Menu(control);
      MenuItem item = new MenuItem(menu, SWT.PUSH);
      item.setText("Cut");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            control.cut();
         }
      });
      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Copy");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            control.copy();
         }
      });
      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Paste");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            control.paste();
         }
      });
      // Add Paste-and-Go menu option
      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Paste-and-Go");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            control.setText("");
            control.paste();
            handleSearch();
         }
      });

      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Select All");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            control.selectAll();
         }
      });

      control.setMenu(menu);
   }

   private void handleSearch() {
      if (!Strings.isValid(searchArea.getText())) {
         AWorkbench.popup("Please enter search string");
         return;
      }
      AtsQuickSearchData data =
         new AtsQuickSearchData("ATS Quick Search", searchArea.getText(), completeCancelledCheck.isChecked());
      Job srchJob = new Job("ATS - Search by ID(s)") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {

            AtsQuickSearchOperation operation = new AtsQuickSearchOperation(data);
            Collection<Artifact> artifacts = operation.performSearch();
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  if (artifacts.isEmpty()) {
                     AWorkbench.popup(data.toString(), data.toString() + "\n\nNo Results Found");
                  } else {
                     if (artifacts.size() == 1 && artifacts.iterator().next().isOfType(
                        AtsArtifactTypes.AbstractWorkflowArtifact)) {
                        WorkflowEditor.editArtifact(artifacts.iterator().next());
                     } else {
                        WorldEditor.open(new WorldEditorSimpleProvider(data.toString(), artifacts));
                     }
                  }
               }
            });
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(srchJob);

   }
}
