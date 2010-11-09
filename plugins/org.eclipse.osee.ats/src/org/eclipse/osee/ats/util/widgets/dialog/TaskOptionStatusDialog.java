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
package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class TaskOptionStatusDialog extends SMAStatusDialog {

   XComboViewer resolutionCombo = new XComboViewer("Resolution");
   private final List<TaskResOptionDefinition> options;
   private final Map<String, TaskResOptionDefinition> nameToResDef = new HashMap<String, TaskResOptionDefinition>();
   private TaskResOptionDefinition selectedOption;
   private final static String MESSAGE = "Enter percent complete and number of hours you spent since last status.";
   private final static String OPTION_MESSAGE =
      "Select resolution, enter percent complete and number of hours you spent since last status.";

   public TaskOptionStatusDialog(Shell parentShell, String dialogTitle, boolean showPercent, List<TaskResOptionDefinition> options, Collection<? extends AbstractWorkflowArtifact> tasks) {
      super(parentShell, dialogTitle, options == null ? MESSAGE : OPTION_MESSAGE, showPercent, tasks);
      this.options = options;
      if (options != null) {
         for (TaskResOptionDefinition trd : options) {
            nameToResDef.put(trd.getName(), trd);
         }
      }
   }

   @Override
   protected IStatus isComplete() {
      TaskResOptionDefinition trd = getSelectedOptionDef();
      int percentComp = percent.getInt();
      if (trd != null && trd.isCompleteable() && percentComp != 100) {
         return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, "Completed resolution must have %Complete == 100");
      }
      if (percentComp == 100 && trd != null && !trd.isCompleteable()) {
         return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID,
            "Can't have 100% complete with a non-Completed resolution");
      }
      return super.isComplete();
   }

   public TaskResOptionDefinition getSelectedOptionDef() {
      return selectedOption;
   }

   @Override
   protected void createPreCustomArea(Composite parent) {
      super.createPreCustomArea(parent);
      if (options != null) {
         resolutionCombo.setLabelProvider(new ResolutionLabelProvider());
         resolutionCombo.setContentProvider(new ArrayContentProvider());
         resolutionCombo.setRequiredEntry(true);
         ArrayList<Object> objs = new ArrayList<Object>();
         for (Object obj : options) {
            objs.add(obj);
         }
         resolutionCombo.setInput(objs);
         resolutionCombo.createWidgets(parent, 2);
         try {
            if (smas.size() == 1) {
               String selOption = smas.iterator().next().getSoleAttributeValue(AtsAttributeTypes.Resolution, "");
               if (Strings.isValid(selOption)) {
                  selectedOption = nameToResDef.get(selOption);
                  if (selectedOption != null) {
                     ArrayList<Object> sel = new ArrayList<Object>();
                     sel.add(selectedOption);
                     resolutionCombo.setSelected(sel);
                  }
               }
            }
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
         resolutionCombo.getCombo().setVisibleItemCount(20);
         resolutionCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               selectedOption = (TaskResOptionDefinition) resolutionCombo.getSelected();
               if (selectedOption != null && !selectedOption.getPercent().equals("")) {
                  int newPercent = Integer.valueOf(selectedOption.getPercent()).intValue();
                  percent.set(newPercent + "");
                  updateStatusLabel();
               }
            };
         });
      }
   }
   public static class ResolutionLabelProvider implements ILabelProvider {

      @Override
      public Image getImage(Object arg0) {
         return null;
      }

      @Override
      public String getText(Object arg0) {
         TaskResOptionDefinition trd = (TaskResOptionDefinition) arg0;
         return trd.getName() + " - " + trd.getDesc() + (trd.isCompleteable() ? " (Completed)" : "");
      }

      @Override
      public void addListener(ILabelProviderListener arg0) {
         // do nothing
      }

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener arg0) {
         // do nothing
      }

   }
}
