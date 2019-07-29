/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.ide.column.ev.WorkPackageColumnUI;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.event.IWfeEventHandle;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeWorkPackage extends Composite implements IWfeEventHandle {

   private final static String WORK_PACKAGE = "Work Package:";
   Text valueLabel;
   Hyperlink link;
   private final IAtsWorkItem workItem;

   public WfeWorkPackage(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData());
      setLayout(ALayout.getZeroMarginLayout(2, false));
      editor.getToolkit().adapt(this);

      try {
         link = editor.getToolkit().createHyperlink(this, WORK_PACKAGE, SWT.NONE);
         if (!workItem.isCancelled() && !workItem.isCompleted()) {
            link.addHyperlinkListener(new IHyperlinkListener() {

               @Override
               public void linkEntered(HyperlinkEvent e) {
                  // do nothing
               }

               @Override
               public void linkExited(HyperlinkEvent e) {
                  // do nothing
               }

               @Override
               public void linkActivated(HyperlinkEvent e) {
                  try {
                     if (editor.isDirty()) {
                        editor.doSave(null);
                     }
                     WorkPackageColumnUI.promptChangeActivityId(workItem, true);
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            });
         } else {
            link.setEnabled(false);
         }

         valueLabel = new Text(this, SWT.NO_TRIM);
         valueLabel.setLayoutData(new GridData());
         editor.getToolkit().adapt(valueLabel, true, true);
         valueLabel.setText("Not Set");
         editor.registerEvent(this, AtsAttributeTypes.WorkPackage, AtsAttributeTypes.WorkPackageReference);
         refresh();

      } catch (OseeCoreException ex) {
         Label errorLabel = editor.getToolkit().createLabel(this, "Error: " + ex.getLocalizedMessage());
         errorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   @Override
   public void refresh() {
      if (Widgets.isAccessible(valueLabel)) {
         String value = "Not Set";
         IAtsWorkPackage workPackage = AtsClientService.get().getEarnedValueService().getWorkPackage(workItem);
         if (workPackage != null) {
            value = workPackage.toString();
         }
         valueLabel.setText(value);
         valueLabel.getParent().layout();
         valueLabel.getParent().getParent().layout();
      }
   }

   @Override
   public void setBackground(Color color) {
      super.setBackground(color);
      if (Widgets.isAccessible(valueLabel)) {
         valueLabel.setBackground(color);
      }
      if (Widgets.isAccessible(link)) {
         link.setBackground(color);
      }
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return workItem;
   }

}
