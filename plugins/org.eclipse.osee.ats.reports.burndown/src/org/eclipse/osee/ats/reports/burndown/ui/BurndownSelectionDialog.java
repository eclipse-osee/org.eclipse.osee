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
import java.util.logging.Level;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.reports.burndown.internal.Activator;
import org.eclipse.osee.ats.reports.burndown.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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

   private final XComboViewer teamCombo = new XComboViewer("Team", SWT.READ_ONLY);
   private final XComboViewer versionCombo = new XComboViewer("Version", SWT.READ_ONLY);
   private final XDate startDateCombo = new XDate("StartDate");
   private final XDate endDateCombo = new XDate("End Date");

   private final IAtsTeamDefinition teamDef;
   private final Active active;

   private Artifact selectedVersion;
   private Artifact selectedTeamDef;
   private Date startDate;
   private Date endDate;

   /**
    * @param active :
    */
   public BurndownSelectionDialog(final Active active) {
      this(null, active);
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
      List<Object> objs = new ArrayList<>();
      try {
         Set<IAtsTeamDefinition> teamReleaseableDefinitions =
            TeamDefinitions.getTeamReleaseableDefinitions(this.active, AtsClientService.get().getQueryService());
         for (IAtsTeamDefinition teamDef : teamReleaseableDefinitions) {
            Artifact artifact = AtsClientService.get().getConfigArtifact(teamDef);
            objs.add(artifact);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      if (teamDef == null) {
         teamCombo.setInput(objs);
         teamCombo.setLabelProvider(new ArtifactDescriptiveLabelProvider());
         teamCombo.setContentProvider(new ArrayContentProvider());
         teamCombo.setSorter(new ArtifactViewerSorter());
         teamCombo.setGrabHorizontal(true);
         teamCombo.createWidgets(comp, 2);
         teamCombo.getCombo().setVisibleItemCount(20);
         teamCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
               widgetSelected(e);
            }

            @Override
            public void widgetSelected(final SelectionEvent e) {
               ArrayList<Object> objs1 = new ArrayList<>();
               try {

                  selectedTeamDef = (Artifact) teamCombo.getSelected();

                  for (Artifact versionArtifact : selectedTeamDef.getRelatedArtifacts(
                     AtsRelationTypes.TeamDefinitionToVersion_Version)) {
                     objs1.add(versionArtifact);
                  }
                  versionCombo.setInput(objs1);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
               validatePage();
            }
         });
      } else {
         Artifact teamArt = null;
         try {
            teamArt = AtsClientService.get().getConfigArtifact(teamDef);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         selectedTeamDef = teamArt;
      }

      versionCombo.setLabelProvider(new ArtifactDescriptiveLabelProvider());
      versionCombo.setContentProvider(new ArrayContentProvider());
      versionCombo.setSorter(new ArtifactViewerSorter());
      versionCombo.setGrabHorizontal(true);
      versionCombo.createWidgets(comp, 2);
      versionCombo.getCombo().setVisibleItemCount(20);
      versionCombo.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetDefaultSelected(final SelectionEvent e) {
            widgetSelected(e);
         }

         @Override
         public void widgetSelected(final SelectionEvent e) {
            selectedVersion = (Artifact) versionCombo.getSelected();
            validatePage();
         }
      });
      if (teamDef != null) {
         objs = new ArrayList<>();
         try {
            for (Artifact versionArtifact : selectedTeamDef.getRelatedArtifacts(
               AtsRelationTypes.TeamDefinitionToVersion_Version)) {
               IAtsVersion version =
                  AtsClientService.get().getCache().getByUuid(versionArtifact.getUuid(), IAtsVersion.class);
               Artifact verArt = AtsClientService.get().getConfigArtifact(version);
               objs.add(verArt);
            }
            versionCombo.setInput(objs);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }

      // Date
      startDateCombo.setFormat("MM/dd/yyyy");
      startDateCombo.setRequiredEntry(true);
      startDateCombo.createWidgets(comp, 2);
      startDateCombo.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(final XWidget widget) {
            startDate = startDateCombo.getDate();
            validatePage();
         }
      });

      endDateCombo.setFormat("MM/dd/yyyy");
      endDateCombo.setRequiredEntry(true);
      endDateCombo.createWidgets(comp, 2);
      endDateCombo.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(final XWidget widget) {
            endDate = endDateCombo.getDate();
            validatePage();
         }
      });
      return container;
   }

   /**
    * Method for validations
    */
   public void validatePage() {
      if (teamCombo.getSelected() == null) {
         setMessage("Please Select a Team");
         getOkButton().setEnabled(false);
         return;
      }
      if (versionCombo.getSelected() == null) {
         setMessage("Please Select a Version");
         getOkButton().setEnabled(false);
         return;
      }
      if (startDateCombo.getDate() == null) {
         setMessage("Please Select the Start Date");
         getOkButton().setEnabled(false);
         return;
      }
      if (endDateCombo.getDate() == null) {
         setMessage("Please Select the End Date");
         getOkButton().setEnabled(false);
         return;
      }
      if (!endDateCombo.getDate().after(startDateCombo.getDate())) {
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
      return selectedVersion;
   }

   /**
    * @return the selectedTeamDef
    */
   public Artifact getSelectedTeamDef() {
      return selectedTeamDef;
   }

   /**
    * @return the start date
    */
   public Date getStartDate() {
      return startDate;
   }

   /**
    * @return the end date
    */
   public Date getEndDate() {
      return endDate;
   }

}
