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

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.WorkspaceURL;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSide;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
   private RelationTypeSide relationGroup;
   private boolean persistOnOk;
   private boolean cancelled = false;

   // Private arrays for valid drops
   private ArrayList<Artifact> validArtifacts;
   private ArrayList<String> urls;
   private ArrayList<String> names;

   // Private arrays for invalid drops
   private ArrayList<String> invalidName;
   private ArrayList<String> invalidReason;
   private ArrayList<Artifact> invalidArtifacts;

   private boolean needWindow;

   private Shell shell;

   private ArtifactType descriptor = null;

   private StructuredViewer viewer;
   private OnCloseListener onCloseListener;

   public static final int ADD_NUM = 0;
   public static final int ARTIFACT_NAME_NUM = 1;
   public static final int ARTIFACT_TYPE_NUM = 2;
   public static final int RATIONALE_NUM = 3;

   public static final int NAME_NUM = 0;
   public static final int REASON_NUM = 1;

   public RelationExplorerWindow(StructuredViewer viewer, RelationTypeSide group, boolean persistOnOk) {
      this.validArtifacts = new ArrayList<Artifact>();
      this.invalidArtifacts = new ArrayList<Artifact>();

      this.urls = new ArrayList<String>();
      this.names = new ArrayList<String>();

      this.invalidName = new ArrayList<String>();
      this.invalidReason = new ArrayList<String>();

      this.viewer = viewer;
      this.relationGroup = group;
      this.persistOnOk = persistOnOk;
      this.needWindow = false;

   }

   public RelationExplorerWindow(StructuredViewer viewer, RelationTypeSide group) {
      this(viewer, group, false);
   }

   public void addValid(Artifact artifact, String url) {
      this.validArtifacts.add(artifact);
      this.urls.add(url);

      if (artifact == null) needWindow = true;

      IFile tempIFile = WorkspaceURL.getIFile(url);
      if (tempIFile != null) {
         String tempName = new String(tempIFile.getName());
         names.add(tempName.substring(0, tempName.length() - (tempIFile.getFileExtension().length() + 1)));
      } else {
         File file = new File(url);
         names.add(file.getName());
      }
   }

   public void addValid(Artifact artifact) {
      this.validArtifacts.add(artifact);
      this.names.add(artifact.getDescriptiveName());
      if (artifact == null) needWindow = true;
   }

   public void addInvalid(String name, String reason) {
      invalidName.add(name);
      invalidReason.add(reason);
      needWindow = true;
   }

   public void createArtifactInformationBox(OnCloseListener onCloseListener) {
      this.onCloseListener = onCloseListener;

      drawWindow();
   }

   private void drawWindow() {
      shell = new Shell(SWT.ON_TOP | SWT.APPLICATION_MODAL | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);

      // Setup Title
      shell.setText("Artifact Information");

      // Setup Icon
      Image image = SkynetGuiPlugin.getInstance().getImage("laser_16_16.gif");
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

      Table validTable =
            new Table(validComposite,
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

      Table invalidTable =
            new Table(invalidComposite,
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
      relationTableViewer = new RelationTableViewer(validTable, invalidTable);
      for (int i = 0; i < validArtifacts.size(); i++)
         relationTableViewer.addValidItem(names.get(i), validArtifacts.get(i));
      for (int i = 0; i < invalidName.size(); i++)
         relationTableViewer.addInvalidItem(invalidName.get(i), invalidReason.get(i));

      // Add Listeners to buttons
      okButton.addSelectionListener(new SelectionListener() {
         public void widgetSelected(SelectionEvent e) {
            okSelected();
         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }
      });

      cancelButton.addSelectionListener(new SelectionListener() {
         public void widgetSelected(SelectionEvent e) {
            cancelSelected();
         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }
      });

      // Add shell resize listener
      shell.addControlListener(new ControlListener() {

         public void controlMoved(ControlEvent e) {
         }

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
                     artifact = descriptor.makeNewArtifact(BranchPersistenceManager.getDefaultBranch());
                     artifact.setSoleAttributeValue("Name", model.getName());
                     artifact.setSoleAttributeValue("Content URL", urls.get(names.indexOf(model.getName())));
                     artifact.persistAttributes();
                  } catch (Exception ex) {
                     OSEELog.logException(SkynetGuiPlugin.class, ex, true);
                  }
               }
            } else
               artifact = model.getArtifact();

            if (artifact != null) {
               try {
                  relationGroup.getArtifact().addRelation(relationGroup, artifact);
               } catch (SQLException ex) {
                  OSEELog.logException(SkynetGuiPlugin.class, ex, true);
               }
            }
         }
      }
      if (persistOnOk) {
         try {
            relationGroup.getArtifact().persistRelations();
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
      shell.dispose();
      viewer.refresh();

      handleListener();

   }

   private void cancelSelected() {
      cancelled = true;
      shell.dispose();

      handleListener();
   }

   private void handleListener() {
      if (onCloseListener != null) onCloseListener.onClose(cancelled);
   }

   /**
    * @return Returns the relationGroup.
    */
   public RelationTypeSide getRelationGroup() {
      return relationGroup;
   }

   /**
    * @return Returns the cancelled.
    */
   public boolean isCancelled() {
      return cancelled;
   }

   /**
    * @return Returns the invalidArtifacts.
    */
   public ArrayList<Artifact> getInvalidArtifacts() {
      return invalidArtifacts;
   }

   /**
    * @param invalidArtifact The invalidArtifact to set.
    */
   public void addInvalidArtifact(Artifact invalidArtifact, String errorMessage) {
      invalidArtifacts.add(invalidArtifact);
      addInvalid(invalidArtifact.getDescriptiveName(), errorMessage);
   }
}