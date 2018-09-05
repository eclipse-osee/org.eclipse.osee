/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.dialog.ViewBranchViewFilterTreeDialog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Megumi Telles
 */
public class XBranchViewSelect extends GenericXWidget {

   private static final String NOT_SELECTED = "--Not Selected--";
   private Text text;
   private Button setBranchViewButton;
   private Artifact artifact;

   public XBranchViewSelect(String label) {
      super(label);
   }

   public XBranchViewSelect(Artifact artifact, String label) {
      super(label);
      this.artifact = artifact;
   }

   @Override
   public Control getControl() {
      return null;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }

      Composite applicabilityComp = new Composite(parent, SWT.NONE);
      applicabilityComp.setLayout(new GridLayout(2, false));
      applicabilityComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false));
      if (toolkit != null) {
         toolkit.adapt(applicabilityComp);
      }

      setBranchViewButton = new Button(applicabilityComp, SWT.PUSH);
      setBranchViewButton.setToolTipText("Select a branch view to associate this version artifact with.");
      setBranchViewButton.setText("Select Branch View");
      setBranchViewButton.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               String baselineBranch = artifact.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchId, "");
               changeView(BranchId.valueOf(baselineBranch));
               refresh();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            //do nothing
         }
      });

      text = new Text(applicabilityComp, SWT.WRAP);
      text.setForeground(Displays.getSystemColor(SWT.COLOR_DARK_GRAY));
      text.setText(getArtifactViewApplicabiltyText());
   }

   private String getArtifactViewApplicabiltyText() {
      String result = NOT_SELECTED;
      ApplicabilityEndpoint applEndpoint = ViewApplicabilityUtil.getApplicabilityEndpoint(artifact.getBranch());
      if (applEndpoint == null) {
         result = "Error: Applicabilty Service not found";
      } else {
         try {
            ArtifactId versionConfig = applEndpoint.getVersionConfig(artifact);
            String baselineBranch = artifact.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchId, "");
            result = ArtifactQuery.getArtifactTokenFromId(BranchId.valueOf(baselineBranch), versionConfig).getName();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            result = "Error retrieving applicability. (see log)";
         }
      }
      return result == null ? NOT_SELECTED : result;
   }

   private boolean changeView(BranchId branch) {
      Map<Long, String> branchViews = ViewApplicabilityUtil.getBranchViews(branch);
      ViewBranchViewFilterTreeDialog dialog =
         new ViewBranchViewFilterTreeDialog("Select Branch View", "Select Branch View", branchViews);
      Collection<String> values = new ArrayList<>();
      values.add("<Clear View Selection>");
      values.addAll(branchViews.values());
      dialog.setInput(values);
      dialog.setMultiSelect(false);
      int result = dialog.open();
      if (result == Window.OK) {
         AtsClientService.getAtsBranchTupleEndpoint().addTuple2(CoreTupleTypes.VersionConfig,
            ArtifactId.valueOf(artifact.getId()), ArtifactId.valueOf(dialog.getSelection()));
         return true;
      }
      return false;
   }

   @Override
   public void refresh() {
      text.setText(getArtifactViewApplicabiltyText());
   }

}
