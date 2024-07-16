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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.DuplicateWidgetUpdateResolver;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workdef.XWidgetPage;
import org.eclipse.osee.ats.ide.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.AttributeFormPart;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactStoredWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * Custom header with metrics bar or overridden by WorkDefinition
 *
 * @author Donald G. Dunne
 */
public class WfeCustomHeader extends Composite {

   protected final IAtsWorkItem workItem;
   private final IManagedForm managedForm;
   private final boolean isEditable;
   private final List<XWidget> allXWidgets = new ArrayList<>();
   private final WorkflowEditor editor;
   private final AbstractWorkflowArtifact wfArt;

   public WfeCustomHeader(Composite parent, int style, IManagedForm managedForm, IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, SWT.NONE);
      this.managedForm = managedForm;
      this.workItem = workItem;
      this.wfArt = (AbstractWorkflowArtifact) workItem;
      this.editor = editor;
      editor.getToolkit().adapt(this);

      isEditable = AtsApiService.get().getAtsAccessService().isWorkflowEditable(workItem);
      // parent.setBackground(Displays.getSystemColor(SWT.COLOR_CYAN));

      try {
         setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
         setLayout(ALayout.getZeroMarginLayout(1, false));
         // section.setBackground(Displays.getSystemColor(SWT.COLOR_GREEN));

         createBody();
         layout();

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private Collection<IAtsLayoutItem> getLayoutItems() {

      // Use custom header definition, if defined
      if (workItem.getWorkDefinition().hasHeaderDefinitionItems()) {
         return workItem.getWorkDefinition().getHeaderDef().getLayoutItems();
      }

      // Else, show default metrics header
      return workItem.getWorkDefinition().getDefaultHeaderDef().getLayoutItems();
   }

   private void createBody() {
      ATSXWidgetOptionResolver optionResolver = ATSXWidgetOptionResolver.getInstance();
      XWidgetPage statePage = new XWidgetPage(workItem, workItem.getWorkDefinition(), optionResolver, getLayoutItems());
      statePage.generateLayoutDatas();

      SwtXWidgetRenderer dynamicXWidgetLayout =
         statePage.createBody(managedForm, this, wfArt, xModListener, isEditable);
      for (XWidget xWidget : dynamicXWidgetLayout.getXWidgets()) {
         addAndCheckChildren(xWidget);
      }

      // Set all XWidget labels to bold font
      XWidgetUtility.setLabelFontsBold(allXWidgets);

      computeTextSizesAndReflow();

   }

   private void computeTextSizesAndReflow() {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof XText) {
            AttributeFormPart.computeXTextSize((XText) widget);
         }
      }
      managedForm.reflow(true);
   }

   private void addAndCheckChildren(XWidget xWidget) {
      allXWidgets.add(xWidget);
      xWidget.addXModifiedListener(xModListener);
      for (XWidget childWidget : xWidget.getChildrenXWidgets()) {
         addAndCheckChildren(childWidget);
      }
   }

   public Result isXWidgetSavable() {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof ArtifactStoredWidget) {
            IStatus status = widget.isValid();
            if (!status.isOK()) {
               return new Result(false, status.getMessage());
            }
         }
      }
      return Result.TrueResult;
   }

   @Override
   public String toString() {
      return "Header for " + workItem.toStringWithId();
   }

   public XResultData isXWidgetDirty(XResultData rd) {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof ArtifactStoredWidget) {
            ArtifactStoredWidget artifactStoredWidget = (ArtifactStoredWidget) widget;
            if (artifactStoredWidget.getArtifact() != null) {
               Result result = artifactStoredWidget.isDirty();
               if (result.isTrue()) {
                  rd.errorf("Widget [%s] is dirty\n", widget.toString());
               }
            }
         }
      }
      return rd;
   }

   public void getDirtyIArtifactWidgets(List<ArtifactStoredWidget> widgets) {
      for (XWidget widget : allXWidgets) {
         if (widget instanceof ArtifactStoredWidget) {
            ArtifactStoredWidget artifactStoredWidget = (ArtifactStoredWidget) widget;
            if (artifactStoredWidget.isDirty().isTrue()) {
               widgets.add(artifactStoredWidget);
            }
         }
      }
   }

   @Override
   public void dispose() {
      for (XWidget xWidget : allXWidgets) {
         if (xWidget != null) {
            xWidget.dispose();
         }
      }
      super.dispose();
   }

   final WfeCustomHeader fSection = this;
   final XModifiedListener xModListener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget xWidget) {
         try {
            if (wfArt.isDeleted()) {
               return;
            }
            // Update duplicate widgets
            DuplicateWidgetUpdateResolver.updateDuplicateWidgets(managedForm, wfArt, xWidget);
            editor.onDirtied();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   };

   public void refresh() {
      try {
         editor.onDirtied();
         for (XWidget xWidget : allXWidgets) {
            xWidget.refresh();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public List<XWidget> getXWidgets(Class<?> clazz) {
      List<XWidget> widgets = new ArrayList<>();
      for (XWidget widget : allXWidgets) {
         if (clazz.isInstance(widget)) {
            widgets.add(widget);
         }
      }
      return widgets;
   }

   public boolean isEditable() {
      return isEditable;
   }

   public WorkflowEditor getEditor() {
      return editor;
   }

   public ArrayList<XWidget> getXWidgets(ArrayList<XWidget> widgets) {
      widgets.addAll(allXWidgets);
      return widgets;
   }

}
