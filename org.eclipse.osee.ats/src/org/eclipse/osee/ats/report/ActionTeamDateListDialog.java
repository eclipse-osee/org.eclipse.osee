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
package org.eclipse.osee.ats.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.ui.skynet.ArtifactViewerSorter;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDescriptiveLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XListViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class ActionTeamDateListDialog extends SelectionDialog {

   XListViewer teamDefList = new XListViewer("Team(s)");
   XDate dateCombo = new XDate("Date");
   XListViewer changeTypeList = new XListViewer("Include Change Types");
   private static ArrayList<Object> selectedTeamDefs;
   private static Date selectedDate;
   private static ArrayList<Object> selectedChangeTypes;

   public ActionTeamDateListDialog(Shell parent) {
      super(parent);
      setTitle("Select Teams and Date");
      setMessage("Select Teams and Date");
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      teamDefList.setLabelProvider(new ArtifactDescriptiveLabelProvider());
      teamDefList.setContentProvider(new ArrayContentProvider());
      teamDefList.setSorter(new ArtifactViewerSorter());
      teamDefList.setGrabHorizontal(true);
      teamDefList.setMultiSelect(true);
      teamDefList.createWidgets(comp, 2);
      if (selectedTeamDefs != null) {
         ArrayList<Object> sel = new ArrayList<Object>();
         for (Object obj : selectedTeamDefs)
            sel.add(obj);
         teamDefList.setSelected(sel);
      }
      Set<Object> objs = new HashSet<Object>();
      try {
         for (TeamDefinitionArtifact teamDef : TeamDefinitionArtifact.getTeamDefinitions(Active.Both))
            objs.add(teamDef);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      teamDefList.setInput(objs);
      teamDefList.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         public void widgetSelected(SelectionEvent e) {
            selectedTeamDefs = teamDefList.getSelected();
         };
      });
      GridData gd = new GridData();
      gd.heightHint = 300;
      teamDefList.getTable().setLayoutData(gd);

      comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      changeTypeList.setLabelProvider(new ChangeType.ChangeTypeLabelProvider());
      changeTypeList.setContentProvider(new ArrayContentProvider());
      Collection<Object> types = new ArrayList<Object>();
      for (ChangeType type : ChangeType.values())
         types.add(type);
      changeTypeList.setInput(types);
      changeTypeList.setGrabHorizontal(true);
      changeTypeList.setMultiSelect(true);
      changeTypeList.createWidgets(comp, 2);
      if (selectedChangeTypes != null) {
         ArrayList<Object> sel = new ArrayList<Object>();
         for (Object obj : selectedChangeTypes)
            sel.add(obj);
         changeTypeList.setSelected(sel);
      }

      changeTypeList.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         public void widgetSelected(SelectionEvent e) {
            selectedChangeTypes = changeTypeList.getSelected();
         };
      });

      comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_BOTH));

      dateCombo.setFormat(XDate.MMDDYY);
      dateCombo.setRequiredEntry(true);
      dateCombo.createWidgets(comp, 2);
      if (selectedDate != null) dateCombo.setDate(selectedDate);
      dateCombo.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            selectedDate = dateCombo.getDate();
         };
      });

      return container;
   }

   public ArrayList<TeamDefinitionArtifact> getSelectedTeamDefs() {
      ArrayList<TeamDefinitionArtifact> adas = new ArrayList<TeamDefinitionArtifact>();
      for (Object obj : selectedTeamDefs)
         adas.add((TeamDefinitionArtifact) obj);
      return adas;
   }

   public Date getSelectedDate() {
      return selectedDate;
   }

   public ArrayList<Object> getSelectedChangeTypes() {
      return selectedChangeTypes;
   }

}
