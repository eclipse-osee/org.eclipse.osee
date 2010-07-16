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
package org.eclipse.osee.framework.ui.skynet.revert;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.RevertJob;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Theron Virgin
 */
public class RevertWizardPage extends WizardPage {

   private final List<List<Artifact>> artifacts;
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.revert.RevertWizardPage";
   public static final String TITLE = "Revert Artifacts Page";
   private TreeViewer treeViewer;
   private Button revertSelectedButton;
   private Button revertAllButton;
   private Combo artifactSelectionBox;
   private final Listener listener = new Listener() {
      public void handleEvent(Event event) {
         try {
            if (event.widget == revertAllButton) {
               List<Artifact> revertList = new LinkedList<Artifact>();
               for (List<Artifact> artList : artifacts) {
                  if (!artList.isEmpty()) {
                     revertList.add(artList.get(0));
                  }
               }
               Jobs.startJob(new RevertJob(revertList));
               artifactSelectionBox.setItems(new String[] {});
               treeViewer.setInput(null);
            }
            if (event.widget == revertSelectedButton) {
               int index = artifactSelectionBox.getSelectionIndex();
               Jobs.startJob(new RevertJob(artifacts.get(index)));
               artifacts.remove(index);
               artifactSelectionBox.remove(index);
               if (artifacts.isEmpty()) {
                  treeViewer.setInput(null);
               } else {
                  treeViewer.setInput(artifacts.get(0));
                  artifactSelectionBox.select(0);
               }
            }
            if (event.widget == revertAllButton || event.widget == revertSelectedButton) {
            }
            if (event.widget == artifactSelectionBox) {
               treeViewer.setInput(artifacts.get(artifactSelectionBox.getSelectionIndex()));
            }
         } catch (Exception ex) {
            OseeLog.log(RevertWizardPage.class, Level.WARNING, ex);
         }
         getWizard().getContainer().updateButtons();
      }
   };

   public RevertWizardPage(List<List<Artifact>> artifacts) {
      super(TITLE);
      this.artifacts = artifacts;
   }

   @Override
   public void createControl(Composite parent) {
      setTitle("The Revert Wizard");
      //      Composite composite = new Composite(parent, SWT.NONE);

      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.heightHint = 300;
      gridData.widthHint = 100;

      parent.setLayout(new GridLayout(1, false));
      parent.setLayoutData(gridData);

      new Label(parent, SWT.LEFT).setText("Artifact to Revert");
      gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.heightHint = 200;
      gridData.widthHint = 75;

      artifactSelectionBox = new Combo(parent, SWT.READ_ONLY);
      List<String> comboValues = new ArrayList<String>();
      for (List<Artifact> artList : artifacts) {
         comboValues.add(artList.get(0).getName());
      }
      artifactSelectionBox.setItems(comboValues.toArray(new String[comboValues.size()]));
      artifactSelectionBox.addListener(SWT.Selection, listener);

      new Label(parent, SWT.LEFT).setText("Revert Effects");

      Composite stackComposite = new Composite(parent, SWT.NONE);
      StackLayout stackLayout = new StackLayout();
      stackComposite.setLayout(stackLayout);
      GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
      data.minimumHeight = 100;
      stackComposite.setLayoutData(data);

      treeViewer = new TreeViewer(stackComposite);
      Tree tree = treeViewer.getTree();
      treeViewer.setContentProvider(new RevertContentProvider(artifactSelectionBox, artifacts));
      treeViewer.setLabelProvider(new RevertLabelProvider(artifactSelectionBox, artifacts));
      treeViewer.addDoubleClickListener(new ArtifactDoubleClick());
      treeViewer.getControl().setLayoutData(gridData);

      treeViewer.setUseHashlookup(false);
      treeViewer.setInput(artifacts != null ? artifacts.get(0) : null);

      TreeEditor myTreeEditor = new TreeEditor(tree);
      myTreeEditor.horizontalAlignment = SWT.LEFT;
      myTreeEditor.grabHorizontal = true;
      myTreeEditor.minimumWidth = 50;
      stackLayout.topControl = treeViewer.getTree();
      stackComposite.layout();
      stackComposite.getParent().layout();

      gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.heightHint = 200;
      gridData.widthHint = 75;
      Composite buttonComposite = new Composite(parent, SWT.NONE);
      buttonComposite.setLayout(new GridLayout(2, false));

      revertSelectedButton = createButton("Revert Selected Artifact", "Show Previous Revert Artifact", buttonComposite);
      revertAllButton = createButton("Revert All Artifacts", "Show Next Revert Artifact", buttonComposite);

      new Label(parent, SWT.LEFT).setText("Cycle through the artifacts and expand the tree to see any unintended \nconsequences of reverting.  \n\nYou must restart OSEE to see the results of the revert.");
      artifactSelectionBox.select(0);
      setControl(parent);

   }

   private Button createButton(String text, String tooltip, Composite composite) {
      Button button = new Button(composite, SWT.PUSH);
      button.addListener(SWT.Selection, listener);
      button.setText(text);
      button.setToolTipText(tooltip);
      return button;
   }

   public boolean canFinish() {
      return true;
   }

   public boolean closingPage() {
      return true;
   }

}
