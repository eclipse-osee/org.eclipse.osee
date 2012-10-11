/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.burndown.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.config.store.TeamDefinitionArtifactStore;
import org.eclipse.osee.ats.core.client.config.store.VersionArtifactStore;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.reports.burndown.Activator;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactViewerSorter;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDescriptiveLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author Praveen Joseph
 */
public class BurndownSelectionDialog extends SelectionDialog {

   XComboViewer teamCombo = new XComboViewer("Team", SWT.READ_ONLY);
   XComboViewer versionCombo = new XComboViewer("Version", SWT.READ_ONLY);
   XDate startDateCombo = new XDate("StartDate");
   XDate endDateCombo = new XDate("End Date");
   Artifact selectedVersion = null;
   Artifact selectedTeamDef = null;
   Date startDate;
   Date endDate;
   private final IAtsTeamDefinition teamDef;
   Artifact project;
   private final Active active;

   /**
    * @param active :
    */
   public BurndownSelectionDialog(final Active active) {
      super(Displays.getActiveShell());
      this.active = active;
      this.teamDef = null;
      setTitle("Select Version");
      setMessage("Select Version");
   }

   /**
    * @param teamDef :
    * @param active :
    */
   public BurndownSelectionDialog(final IAtsTeamDefinition teamDef, final Active active) {
      super(Displays.getActiveShell());
      this.teamDef = teamDef;
      this.active = active;
      setTitle("Select Version");
      setMessage("Select Version");
   }

   @Override
   protected Control createDialogArea(final Composite container) {

      List<Object> objs = new ArrayList<Object>();
      try {
         Set<IAtsTeamDefinition> teamReleaseableDefinitions =
            TeamDefinitions.getTeamReleaseableDefinitions(this.active);
         for (IAtsTeamDefinition art : teamReleaseableDefinitions) {
            TeamDefinitionArtifactStore artifactStore = new TeamDefinitionArtifactStore(art);
            Artifact artifact = artifactStore.getArtifact();
            objs.add(artifact);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      if (this.teamDef == null) {
         this.teamCombo.setInput(objs);
         this.teamCombo.setLabelProvider(new ArtifactDescriptiveLabelProvider());
         this.teamCombo.setContentProvider(new ArrayContentProvider());
         this.teamCombo.setSorter(new ArtifactViewerSorter());
         this.teamCombo.setGrabHorizontal(true);
         this.teamCombo.createWidgets(comp, 2);
         this.teamCombo.getCombo().setVisibleItemCount(20);
         this.teamCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
               widgetSelected(e);
            }

            @Override
            public void widgetSelected(final SelectionEvent e) {
               ArrayList<Object> objs1 = new ArrayList<Object>();
               try {

                  BurndownSelectionDialog.this.selectedTeamDef =
                     (Artifact) BurndownSelectionDialog.this.teamCombo.getSelected();

                  for (Artifact versionArtifact : BurndownSelectionDialog.this.selectedTeamDef.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
                     objs1.add(versionArtifact);
                  }
                  BurndownSelectionDialog.this.versionCombo.setInput(objs1);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
               validatePage();
            }
         });
      } else {
         TeamDefinitionArtifactStore store = new TeamDefinitionArtifactStore(this.teamDef);
         Artifact teamArt = null;
         try {
            teamArt = store.getArtifact();
         } catch (OseeCoreException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
         this.selectedTeamDef = teamArt;
      }

      this.versionCombo.setLabelProvider(new ArtifactDescriptiveLabelProvider());
      this.versionCombo.setContentProvider(new ArrayContentProvider());
      this.versionCombo.setSorter(new ArtifactViewerSorter());
      this.versionCombo.setGrabHorizontal(true);
      this.versionCombo.createWidgets(comp, 2);
      this.versionCombo.getCombo().setVisibleItemCount(20);
      this.versionCombo.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetDefaultSelected(final SelectionEvent e) {
            widgetSelected(e);
         }

         @Override
         public void widgetSelected(final SelectionEvent e) {
            BurndownSelectionDialog.this.selectedVersion =
               (Artifact) BurndownSelectionDialog.this.versionCombo.getSelected();
            validatePage();
         }
      });
      if (this.teamDef != null) {
         objs = new ArrayList<Object>();
         try {
            for (Artifact versionArtifact : this.selectedTeamDef.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
               IAtsVersion version =
                  AtsConfigCache.instance.getSoleByGuid(versionArtifact.getGuid(), IAtsVersion.class);
               VersionArtifactStore store = new VersionArtifactStore(version);
               Artifact verArt = store.getArtifact();
               objs.add(verArt);
            }
            this.versionCombo.setInput(objs);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }

      // Date
      this.startDateCombo.setFormat("MM/dd/yyyy");
      this.startDateCombo.setRequiredEntry(true);
      this.startDateCombo.createWidgets(comp, 2);
      this.startDateCombo.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(final XWidget widget) {
            BurndownSelectionDialog.this.startDate = BurndownSelectionDialog.this.startDateCombo.getDate();
            validatePage();
         }
      });

      this.endDateCombo.setFormat("MM/dd/yyyy");
      this.endDateCombo.setRequiredEntry(true);
      this.endDateCombo.createWidgets(comp, 2);
      this.endDateCombo.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(final XWidget widget) {
            BurndownSelectionDialog.this.endDate = BurndownSelectionDialog.this.endDateCombo.getDate();
            validatePage();
         }
      });
      return container;
   }

   /**
    * Method for validations
    */
   public void validatePage() {
      if (this.teamCombo.getSelected() == null) {
         setMessage("Please Select a Team");
         getOkButton().setEnabled(false);
         return;
      }
      if (this.versionCombo.getSelected() == null) {
         setMessage("Please Select a Version");
         getOkButton().setEnabled(false);
         return;
      }
      if (this.startDateCombo.getDate() == null) {
         setMessage("Please Select the Start Date");
         getOkButton().setEnabled(false);
         return;
      }
      if (this.endDateCombo.getDate() == null) {
         setMessage("Please Select the End Date");
         getOkButton().setEnabled(false);
         return;
      }
      if (!this.endDateCombo.getDate().after(this.startDateCombo.getDate())) {
         setMessage("End Date must occur after Start Date!!");
         getOkButton().setEnabled(false);
         return;
      }
      setMessage(null);
      getOkButton().setEnabled(true);
   }

   /**
    * @return selected version
    */
   public Artifact getSelectedVersion() {
      return this.selectedVersion;
   }

   /**
    * @return the selectedTeamDef
    */
   public Artifact getSelectedTeamDef() {
      return this.selectedTeamDef;
   }

   /**
    * @return the start date
    */
   public Date getStartDate() {
      return this.startDate;
   }

   /**
    * @return the end date
    */
   public Date getEndDate() {
      return this.endDate;
   }

}
