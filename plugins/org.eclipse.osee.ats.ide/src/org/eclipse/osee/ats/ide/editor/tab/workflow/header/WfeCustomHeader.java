/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workdef.XWidgetWorkDefRenderer;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XWidgetUtility;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * Custom header with change type, points, priority, needby; overridden/extended by WorkDefinition
 *
 * @author Donald G. Dunne
 */
public class WfeCustomHeader extends Composite {

   protected final IAtsWorkItem workItem;
   private final IManagedForm managedForm;
   private final List<XWidget> widgets = new ArrayList<>();
   private final WorkflowEditor editor;
   private final AbstractWorkflowArtifact wfArt;
   private final boolean debugLayout = false; // turns on background colors for composites

   public WfeCustomHeader(Composite parent, int style, IManagedForm managedForm, IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, SWT.NONE);
      this.managedForm = managedForm;
      this.workItem = workItem;
      this.wfArt = (AbstractWorkflowArtifact) workItem;
      this.editor = editor;
      editor.getToolkit().adapt(this);

      if (debugLayout) {
         parent.setBackground(Displays.getSystemColor(SWT.COLOR_RED));
         parent.getParent().setBackground(Displays.getSystemColor(SWT.COLOR_YELLOW));
      }

      try {
         GridData gd = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
         gd.horizontalIndent = 0; // Want to align the left with rest of widgets
         setLayoutData(gd);
         // zero margin so childComposites don't indent
         setLayout(ALayout.getZeroMarginLayout(2, false));
         if (debugLayout) {
            setBackground(Displays.getSystemColor(SWT.COLOR_GREEN));
         }

         createBody();
         layout();

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private Collection<LayoutItem> getLayoutItems() {

      // Use custom header definition, if defined
      if (workItem.getWorkDefinition().hasHeaderDefinitionItems()) {
         return workItem.getWorkDefinition().getHeaderDef().getLayoutItems();
      }

      // Else, show default metrics header
      return workItem.getWorkDefinition().getDefaultHeaderDef().getLayoutItems();
   }

   private void createBody() {

      XWidgetWorkDefRenderer workDefPage =
         new XWidgetWorkDefRenderer(workItem, workItem.getWorkDefinition(), null, getLayoutItems());
      workDefPage.createBody(managedForm, this, wfArt, null, true);
      widgets.addAll(workDefPage.getXWidgets());

      computeTextSizesAndReflow();
   }

   private void computeTextSizesAndReflow() {
      for (XWidget widget : widgets) {
         if (widget instanceof XXTextWidget) {
            XWidgetUtility.computeXTextSize((XXTextWidget) widget);
         }
      }
      managedForm.reflow(true);
   }

   @Override
   public String toString() {
      return "Header for " + workItem.toStringWithId();
   }

   @Override
   public void dispose() {
      for (XWidget xWidget : widgets) {
         if (xWidget != null) {
            xWidget.dispose();
         }
      }
      super.dispose();
   }

   final WfeCustomHeader fSection = this;

   public void refresh() {
      try {
         editor.onDirtied();
         for (XWidget xWidget : widgets) {
            xWidget.refresh();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public List<XWidget> getXWidgets(Class<?> clazz) {
      List<XWidget> classWidgets = new ArrayList<>();
      for (XWidget widget : widgets) {
         if (clazz.isInstance(widget)) {
            classWidgets.add(widget);
         }
      }
      return classWidgets;
   }

   public WorkflowEditor getEditor() {
      return editor;
   }

   public List<XWidget> getXWidgets() {
      return widgets;
   }

}
