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
package org.eclipse.osee.framework.ui.skynet.relation.explorer;

import java.util.ArrayList;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * @author Michael S. Rodgers
 */
public class RelationExplorerWindow {

   private RelationTableViewer relationTableViewer;
   private final RelationTypeSideSorter relationGroup;
   private final boolean persistOnOk;
   private boolean cancelled = false;

   // Private arrays for valid drops
   private final ArrayList<Artifact> validArtifacts;
   private final ArrayList<String> urls;
   private final ArrayList<String> names;
   private BranchId branch;

   // Private arrays for invalid drops
   private final ArrayList<String> invalidName;
   private final ArrayList<String> invalidReason;
   private final ArrayList<Artifact> invalidArtifacts;

   private boolean needWindow;

   private Shell shell;

   private ArtifactTypeToken descriptor = null;

   private final StructuredViewer viewer;

   public static final int ADD_NUM = 0;
   public static final int ARTIFACT_NAME_NUM = 1;
   public static final int ARTIFACT_TYPE_NUM = 2;
   public static final int RATIONALE_NUM = 3;

   public static final int NAME_NUM = 0;
   public static final int REASON_NUM = 1;

   public RelationExplorerWindow(StructuredViewer viewer, RelationTypeSideSorter group, boolean persistOnOk) {
      this.validArtifacts = new ArrayList<>();
      this.invalidArtifacts = new ArrayList<>();

      this.urls = new ArrayList<>();
      this.names = new ArrayList<>();

      this.invalidName = new ArrayList<>();
      this.invalidReason = new ArrayList<>();

      this.viewer = viewer;
      this.relationGroup = group;
      this.persistOnOk = persistOnOk;
      this.needWindow = false;

   }

   public RelationExplorerWindow(StructuredViewer viewer, RelationTypeSideSorter group) {
      this(viewer, group, false);
   }

   public void addValid(Artifact artifact) {
      if (artifact == null) {
         needWindow = true;
      } else {
         this.branch = artifact.getBranch();
         this.validArtifacts.add(artifact);
         this.names.add(artifact.getName());
      }
   }

   public void addInvalid(String name, String reason) {
      invalidName.add(name);
      invalidReason.add(reason);
      needWindow = true;
   }

   public void createArtifactInformationBox() {
      drawWindow();
   }

   private void drawWindow() {
      shell = new Shell(SWT.ON_TOP | SWT.APPLICATION_MODAL | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);

      // Setup Title
      shell.setText("Artifact Information");

      // Setup Icon
      Image image = ArtifactImageManager.getImage(CoreArtifactTypes.Artifact);
      shell.setImage(image);

      // Setup Form Layout
      FormLayout layout = new FormLayout();
      layout.marginHeight = 5;
      layout.marginWidth = 5;
      shell.setLayout(layout);

      SashForm sashForm = new SashForm(shell, SWT.VERTICAL);

      // Create valid artifact fields
      FormLayout validLayout = new FormLayout();
      validLayout.spacing = 5;

      Composite validComposite = new Composite(sashForm, SWT.NONE);
      validComposite.setLayout(validLayout);

      Label validLabel = new Label(validComposite, SWT.LEFT);
      validLabel.setText("Valid artifacts - will be added");

      Table validTable = new Table(validComposite,
         SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
      validTable.setLinesVisible(true);
      validTable.setHeaderVisible(true);

      // Create invalid artifacts fields
      FormLayout invalidLayout = new FormLayout();
      invalidLayout.spacing = 5;

      Composite invalidComposite = new Composite(sashForm, SWT.NONE);
      invalidComposite.setLayout(invalidLayout);

      Label invalidLabel = new Label(invalidComposite, SWT.LEFT);
      invalidLabel.setText("Invalid artifacts - will not be added");

      Table invalidTable = new Table(invalidComposite,
         SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
      invalidTable.setLinesVisible(true);
      invalidTable.setHeaderVisible(true);

      // Create the Buttons
      Button okButton = new Button(invalidComposite, SWT.PUSH);
      okButton.setText("OK");

      Button cancelButton = new Button(invalidComposite, SWT.PUSH);
      cancelButton.setText("Cancel");

      // Attach validLabel to top-left corner
      FormData data = new FormData();
      data.top = new FormAttachment(0);
      data.left = new FormAttachment(0);
      validLabel.setLayoutData(data);

      // Attach validTable to bottom of validLabel
      data = new FormData();
      data.top = new FormAttachment(validLabel);
      data.bottom = new FormAttachment(100);
      data.left = new FormAttachment(0);
      data.right = new FormAttachment(100);
      data.height = validTable.getItemHeight() * 10;
      validTable.setLayoutData(data);

      // Attach invalidLabel to top-left corner
      data = new FormData();
      data.top = new FormAttachment(0);
      data.left = new FormAttachment(0);
      invalidLabel.setLayoutData(data);

      // Attach invalidTable to bottom of invalidLabel
      data = new FormData();
      data.top = new FormAttachment(invalidLabel);
      data.bottom = new FormAttachment(okButton);
      data.left = new FormAttachment(0);
      data.right = new FormAttachment(100);
      data.height = validTable.getItemHeight() * 10;
      invalidTable.setLayoutData(data);

      // Attach sashForm to top-left corner of shell
      data = new FormData();
      data.top = new FormAttachment(0);
      data.bottom = new FormAttachment(100);
      data.left = new FormAttachment(0);
      data.right = new FormAttachment(100);
      sashForm.setLayoutData(data);

      // Attach buttons to bottom of sashForm
      data = new FormData();
      data.bottom = new FormAttachment(100);
      data.right = new FormAttachment(100);
      cancelButton.setLayoutData(data);

      data = new FormData();
      data.bottom = new FormAttachment(100);
      data.right = new FormAttachment(cancelButton);
      okButton.setLayoutData(data);

      // Populate Tables
      relationTableViewer = new RelationTableViewer(validTable, invalidTable, branch);
      for (int i = 0; i < validArtifacts.size(); i++) {
         relationTableViewer.addValidItem(validArtifacts.get(i));
      }
      for (int i = 0; i < invalidName.size(); i++) {
         relationTableViewer.addInvalidItem(invalidName.get(i), invalidReason.get(i));
      }

      // Add Listeners to buttons
      okButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            okSelected();
         }

      });

      cancelButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            cancelSelected();
         }

      });

      // Add shell resize listener
      shell.addControlListener(new ControlListener() {

         @Override
         public void controlMoved(ControlEvent e) {
            // do nothing
         }

         @Override
         public void controlResized(ControlEvent e) {
            relationTableViewer.resizeTable(((Shell) e.widget).getClientArea().width);
            shell.layout();
         }
      });

      if (needWindow) {
         shell.pack();
         shell.open();
      } else {
         okSelected();
      }
   }

   /**
    * Create the TableViewer
    */

   private void okSelected() {
      ArrayList<ArtifactModel> artifactList = relationTableViewer.getArtifactList().getArtifactModel();

      for (int i = 0; i < artifactList.size(); i++) {
         ArtifactModel model = artifactList.get(i);

         if (model.isAdd()) {
            Artifact artifact = model.getArtifact();
            descriptor = model.getDescriptor();
            if (artifact == null) {
               if (descriptor != null) {
                  try {
                     artifact = ArtifactTypeManager.addArtifact(descriptor, branch);
                     artifact.setName(model.getName());
                     artifact.setSoleAttributeValue(CoreAttributeTypes.ContentUrl,
                        urls.get(names.indexOf(model.getName())));
                     artifact.persist(getClass().getSimpleName());
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            } else {
               artifact = model.getArtifact();
            }

            if (artifact != null) {
               try {
                  relationGroup.getArtifact().addRelation(relationGroup, artifact);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      }
      if (persistOnOk) {
         try {
            relationGroup.getArtifact().persist(getClass().getSimpleName());
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      shell.dispose();
      viewer.refresh();

   }

   private void cancelSelected() {
      cancelled = true;
      shell.dispose();
   }

   public RelationTypeSideSorter getRelationGroup() {
      return relationGroup;
   }

   public boolean isCancelled() {
      return cancelled;
   }

   public ArrayList<Artifact> getInvalidArtifacts() {
      return invalidArtifacts;
   }

   public void addInvalidArtifact(Artifact invalidArtifact, String errorMessage) {
      invalidArtifacts.add(invalidArtifact);
      addInvalid(invalidArtifact.getName(), errorMessage);
   }
}