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
package org.eclipse.osee.framework.ui.skynet.search.page;

import org.eclipse.osee.framework.ui.skynet.search.page.actions.BranchRevisionListener;
import org.eclipse.osee.framework.ui.skynet.search.page.actions.BranchSelectionListener;
import org.eclipse.osee.framework.ui.skynet.search.page.actions.ListSelectionListener;
import org.eclipse.osee.framework.ui.skynet.search.page.actions.RevisionVerificationListener;
import org.eclipse.osee.framework.ui.skynet.search.page.actions.TreeNodeCheckStateListener;
import org.eclipse.osee.framework.ui.skynet.search.page.actions.TreeRefreshListener;
import org.eclipse.osee.framework.ui.skynet.search.page.manager.DataManager;
import org.eclipse.osee.framework.ui.skynet.search.page.manager.IDataListener;
import org.eclipse.osee.framework.ui.skynet.search.page.widget.ArtifactTreeSearchWidget;
import org.eclipse.osee.framework.ui.skynet.search.page.widget.ArtifactTypeListWidget;
import org.eclipse.osee.framework.ui.skynet.search.page.widget.IViewer;
import org.eclipse.osee.framework.ui.skynet.search.page.widget.RevisionSelectionWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class ArtifactSearchComposite extends Composite implements IViewer {

   private ArtifactTypeListWidget artifactTypeListWidget;
   private ArtifactTreeSearchWidget artifactTreeSearchWidget;
   private RevisionSelectionWidget revisionSelectionWidget;
   private Button addButton;
   private DataManager revisionDataManager;

   public ArtifactSearchComposite(Composite parent, int style) {
      super(parent, style);
      create();
   }

   private void create() {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      this.setFont(this.getParent().getFont());

      revisionDataManager = new DataManager();
      createBranchRevisionArea(this);

      SashForm sashForm = new SashForm(this, SWT.HORIZONTAL);
      sashForm.SASH_WIDTH = 3;
      sashForm.setLayout(new GridLayout());
      sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      createArtifactSelectArea(sashForm);
      createArtifactTreeSearchArea(sashForm);
      sashForm.setWeights(new int[] {1, 2});
      attachActions();
   }

   private void createBranchRevisionArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setText("Select a Branch and Revision");

      revisionSelectionWidget = new RevisionSelectionWidget(composite, SWT.NONE);
   }

   private void createArtifactSelectArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout(2, false));
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      group.setText("Select Artifact Types");
      group.setToolTipText("Select Artifact Types from list to add to Tree Search Area.");

      artifactTypeListWidget = new ArtifactTypeListWidget(group, SWT.NONE);

      addButton = new Button(group, SWT.PUSH);
      addButton.setText(">");
   }

   private void createArtifactTreeSearchArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setText("Select Artifact Types and Attributes to Search");
      group.setToolTipText("Super Explicit Instructions...");
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      artifactTreeSearchWidget = new ArtifactTreeSearchWidget(group, SWT.NONE);
   }

   public ArtifactTreeSearchWidget getTreeWidget() {
      return artifactTreeSearchWidget;
   }

   public ArtifactTypeListWidget getListWidget() {
      return artifactTypeListWidget;
   }

   public RevisionSelectionWidget getRevisionWidget() {
      return revisionSelectionWidget;
   }

   public void addSelectionListener(SelectionListener listener) {
      if (addButton != null && !addButton.isDisposed()) {
         addButton.addSelectionListener(listener);
      }
   }

   public void addRevisionDataListener(IDataListener listener) {
      if (this.revisionDataManager != null) {
         this.revisionDataManager.addDataListener(listener);
      }
   }

   @Override
   public void dispose() {
      super.dispose();
      artifactTreeSearchWidget.dispose();
      artifactTypeListWidget.dispose();
      revisionSelectionWidget.dispose();
   }

   public void refresh() {
      artifactTreeSearchWidget.refresh();
      artifactTypeListWidget.refresh();
      revisionSelectionWidget.refresh();
   }

   private void attachActions() {
      new BranchSelectionListener(this);
      new RevisionVerificationListener(this);
      new ListSelectionListener(this);
      new BranchRevisionListener(this);
      new TreeNodeCheckStateListener(this);
      new TreeRefreshListener(this);
   }

   public DataManager getRevisionDataManager() {
      return revisionDataManager;
   }

}
