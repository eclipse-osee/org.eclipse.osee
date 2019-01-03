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
package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.ArrayList;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
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
 * @author Donald G. Dunne
 */
public class TeamVersionListDialog extends SelectionDialog {

   XComboViewer teamCombo = new XComboViewer("Team", SWT.READ_ONLY);
   XComboViewer versionCombo = new XComboViewer("Version", SWT.READ_ONLY);
   IAtsVersion selectedVersion = null;
   IAtsTeamDefinition selectedTeamDef = null;
   private final Active active;
   private final IAtsTeamDefinition teamDef;

   public TeamVersionListDialog(Active active) {
      super(Displays.getActiveShell());
      this.active = active;
      this.teamDef = null;
      setTitle("Select Version");
      setMessage("Select Version");
   }

   @Override
   protected Control createDialogArea(Composite container) {

      ArrayList<Object> objs = new ArrayList<>();
      try {
         for (IAtsTeamDefinition art : TeamDefinitions.getTeamReleaseableDefinitions(active,
            AtsClientService.get().getQueryService())) {
            objs.add(art);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      if (teamDef == null) {
         teamCombo.setInput(objs);
         teamCombo.setLabelProvider(new AtsObjectLabelProvider());
         teamCombo.setContentProvider(new ArrayContentProvider());
         teamCombo.setComparator(new AtsObjectNameSorter());
         teamCombo.setGrabHorizontal(true);
         teamCombo.createWidgets(comp, 2);
         teamCombo.getCombo().setVisibleItemCount(20);
         teamCombo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
               widgetSelected(e);
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
               ArrayList<Object> objs = new ArrayList<>();
               try {
                  selectedTeamDef = (IAtsTeamDefinition) teamCombo.getSelected();
                  for (IAtsVersion pda : selectedTeamDef.getVersions(VersionReleaseType.Both, VersionLockedType.Both)) {
                     objs.add(pda);
                  }
                  versionCombo.setInput(objs);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            };
         });
      } else {
         selectedTeamDef = teamDef;
      }

      versionCombo.setLabelProvider(new AtsObjectLabelProvider());
      versionCombo.setContentProvider(new ArrayContentProvider());
      versionCombo.setComparator(new AtsObjectNameSorter());
      versionCombo.setGrabHorizontal(true);
      versionCombo.createWidgets(comp, 2);
      versionCombo.getCombo().setVisibleItemCount(20);
      versionCombo.addSelectionListener(new SelectionListener() {
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         @Override
         public void widgetSelected(SelectionEvent e) {
            selectedVersion = (IAtsVersion) versionCombo.getSelected();
         };
      });
      if (teamDef != null) {
         objs = new ArrayList<>();
         try {
            for (IAtsVersion pda : teamDef.getVersions(VersionReleaseType.Both, VersionLockedType.Both)) {
               objs.add(pda);
            }
            versionCombo.setInput(objs);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }

      return container;
   }

   public IAtsVersion getSelectedVersion() {
      return selectedVersion;
   }

   /**
    * @return the selectedTeamDef
    */
   public IAtsTeamDefinition getSelectedTeamDef() {
      return selectedTeamDef;
   }

}
