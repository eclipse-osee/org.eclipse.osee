/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.dispo;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class ImportCoverageMethodsBlam extends AbstractBlam {

   private final String FROM_BRANCH_WIDGET_NAME = "Select the FROM branch";
   private final String FROM_PACKAGE_ARTIFACT = "Select the FROM Coverage Package";
   private final String TO_BRANCH_WIDGET_NAME = "Select the TO branch";
   private final String TO_PACKAGE_ARTIFACT = "Select the TO Coverage Package";
   private final String CHKBOX_PERSIST_TRANSACTION = "Execute transaction";
   private final String CHKBOX_RETAIN_TASK_TRACKING = "Retain Work Product Task Tracking Information";
   private final String CHKBOX_FORCE_METHOD_NUMBERS = "Force method number";
   public static String RESULTS_DIR = "Results Directory";

   private XBranchSelectWidget fromBranchWidget = null;
   private XComboViewer fromPackageListWidget;
   private XBranchSelectWidget toBranchWidget = null;
   private XComboViewer toPackageListWidget;

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"" + FROM_BRANCH_WIDGET_NAME + "\"/>");
      builder.append("<XWidget xwidgetType=\"XComboViewer\" displayName=\"" + FROM_PACKAGE_ARTIFACT + "\" />");
      builder.append("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"" + TO_BRANCH_WIDGET_NAME + "\"/>");
      builder.append("<XWidget xwidgetType=\"XComboViewer\" displayName=\"" + TO_PACKAGE_ARTIFACT + "\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + CHKBOX_PERSIST_TRANSACTION + "\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + CHKBOX_RETAIN_TASK_TRACKING + "\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + CHKBOX_FORCE_METHOD_NUMBERS + "\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("<XWidget xwidgetType=\"XDirectorySelectionDialog\" displayName=\"" + RESULTS_DIR + "\" defaultValue=\"C:\\UserData\\CoverageMerge\\\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws OseeCoreException {
      Artifact fromPackageArt = variableMap.getArtifact(FROM_PACKAGE_ARTIFACT);
      Artifact toPackageArt = variableMap.getArtifact(TO_PACKAGE_ARTIFACT);
      if (fromPackageArt == null) {
         AWorkbench.popup("Must select FROM Coverage Package");
         monitor.setCanceled(true);
         return;
      }
      if (toPackageArt == null) {
         AWorkbench.popup("Must select TO Coverage Package");
         monitor.setCanceled(true);
         return;
      }
      String resultsDir = variableMap.getString(RESULTS_DIR);
      if (!Strings.isValid(resultsDir)) {
         AWorkbench.popup("Must select results directory");
         monitor.setCanceled(true);
         return;
      }
      boolean isPersistTransaction = variableMap.getBoolean(CHKBOX_PERSIST_TRANSACTION);
      boolean isRetainTaskTracking = variableMap.getBoolean(CHKBOX_RETAIN_TASK_TRACKING);
      boolean forMethodNumbers = variableMap.getBoolean(CHKBOX_FORCE_METHOD_NUMBERS);
      ImportCoverageMethodsOperation operation =
         new ImportCoverageMethodsOperation(fromPackageArt, toPackageArt, resultsDir, isPersistTransaction,
            isRetainTaskTracking, forMethodNumbers);
      Operations.executeWork(operation, monitor);
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(TO_BRANCH_WIDGET_NAME)) {
         toBranchWidget = (XBranchSelectWidget) xWidget;
         toBranchWidget.addListener(new Listener() {
            @Override
            public void handleEvent(Event event) {
               updateToPackageListWidget();
            }

         });
      }
      if (xWidget.getLabel().equalsIgnoreCase(TO_PACKAGE_ARTIFACT)) {
         toPackageListWidget = (XComboViewer) xWidget;
      }
      if (xWidget.getLabel().equals(FROM_BRANCH_WIDGET_NAME)) {
         fromBranchWidget = (XBranchSelectWidget) xWidget;
         fromBranchWidget.addListener(new Listener() {
            @Override
            public void handleEvent(Event event) {
               IOseeBranch fromBranch = fromBranchWidget.getSelection();
               toBranchWidget.setSelection(fromBranch);
               updateToPackageListWidget();
               try {
                  if (fromPackageListWidget != null) {
                     fromPackageListWidget.setContentProvider(new ArtifactContentProvider());
                     fromPackageListWidget.setLabelProvider(new ArtifactLabelProvider());
                     List<Object> arts = new LinkedList<Object>();
                     arts.addAll(OseeCoveragePackageStore.getCoveragePackageArtifacts(fromBranch));
                     fromPackageListWidget.setInput(arts);
                     fromPackageListWidget.setEditable(true);
                     fromPackageListWidget.refresh();
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }

            }
         });
      }
      if (xWidget.getLabel().equalsIgnoreCase(FROM_PACKAGE_ARTIFACT)) {
         fromPackageListWidget = (XComboViewer) xWidget;
      }
   }

   private void updateToPackageListWidget() {
      IOseeBranch toBranch = toBranchWidget.getSelection();
      try {
         if (toPackageListWidget != null) {
            toPackageListWidget.setContentProvider(new ArtifactContentProvider());
            toPackageListWidget.setLabelProvider(new ArtifactLabelProvider());
            List<Object> arts = new LinkedList<Object>();
            arts.addAll(OseeCoveragePackageStore.getCoveragePackageArtifacts(toBranch));
            toPackageListWidget.setInput(arts);
            toPackageListWidget.setEditable(true);
            toPackageListWidget.refresh();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String getName() {
      return "Import Coverage Methods";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

}
