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
package org.eclipse.osee.ats.navigate.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsObjectLabelProvider;
import org.eclipse.osee.ats.util.widgets.dialog.AtsObjectNameSorter;
import org.eclipse.osee.ats.workflow.ChangeTypeLabelProvider;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
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
   private static List<Object> selectedTeamDefs;
   private static Date selectedDate;
   private static List<Object> selectedChangeTypes;

   public ActionTeamDateListDialog(Shell parent) {
      super(parent);
      setTitle("Select Teams and Date");
      setMessage("Select Teams and Date");
   }

   @Override
   protected Control createDialogArea(Composite container) {

      createTeamDefList(container);
      createChangeTypeList(container);
      createDateCombo(container);

      return container;
   }

   private void createDateCombo(Composite container) {
      Composite comp;
      comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_BOTH));

      dateCombo.setFormat(DateUtil.MMDDYY);
      dateCombo.setRequiredEntry(true);
      dateCombo.createWidgets(comp, 2);
      if (selectedDate != null) {
         dateCombo.setDate(selectedDate);
      }
      dateCombo.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            selectedDate = dateCombo.getDate();
         };
      });
   }

   private void createChangeTypeList(Composite container) {
      Composite comp;
      comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      changeTypeList.setLabelProvider(new ChangeTypeLabelProvider());
      changeTypeList.setContentProvider(new ArrayContentProvider());
      Collection<Object> types = new ArrayList<Object>();
      for (ChangeType type : ChangeType.values()) {
         types.add(type);
      }
      changeTypeList.setInput(types);
      changeTypeList.setGrabHorizontal(true);
      changeTypeList.setMultiSelect(true);
      changeTypeList.createWidgets(comp, 2);
      if (selectedChangeTypes != null) {
         ArrayList<Object> sel = new ArrayList<Object>();
         for (Object obj : selectedChangeTypes) {
            sel.add(obj);
         }
         changeTypeList.setSelected(sel);
      }

      changeTypeList.addSelectionListener(new SelectionListener() {
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         @Override
         public void widgetSelected(SelectionEvent e) {
            selectedChangeTypes = changeTypeList.getSelected();
         };
      });
   }

   private void createTeamDefList(Composite container) {
      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      teamDefList.setLabelProvider(new AtsObjectLabelProvider());
      teamDefList.setContentProvider(new ArrayContentProvider());
      teamDefList.setSorter(new AtsObjectNameSorter());
      teamDefList.setGrabHorizontal(true);
      teamDefList.setMultiSelect(true);
      teamDefList.createWidgets(comp, 2);
      if (selectedTeamDefs != null) {
         ArrayList<Object> sel = new ArrayList<Object>();
         for (Object obj : selectedTeamDefs) {
            sel.add(obj);
         }
         teamDefList.setSelected(sel);
      }
      Set<Object> objs = new HashSet<Object>();
      try {
         for (IAtsTeamDefinition teamDef : TeamDefinitions.getTeamDefinitions(Active.Both,
            AtsClientService.get().getConfig())) {
            objs.add(teamDef);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      teamDefList.setInput(objs);
      teamDefList.addSelectionListener(new SelectionListener() {
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         @Override
         public void widgetSelected(SelectionEvent e) {
            selectedTeamDefs = teamDefList.getSelected();
         };
      });
      GridData gd = new GridData();
      gd.heightHint = 300;
      teamDefList.getTable().setLayoutData(gd);
   }

   public List<IAtsTeamDefinition> getSelectedTeamDefs() {
      ArrayList<IAtsTeamDefinition> adas = new ArrayList<IAtsTeamDefinition>();
      for (Object obj : selectedTeamDefs) {
         adas.add((IAtsTeamDefinition) obj);
      }
      return adas;
   }

   public Date getSelectedDate() {
      return selectedDate;
   }

   public List<Object> getSelectedChangeTypes() {
      return selectedChangeTypes;
   }

}
