/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.pages;

import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class ArtifactFormPageViewApplicability {

   private FormText text;
   private static Image LOCK_IMAGE;
   private Button button;
   private final FormToolkit toolkit;
   private final ScrolledForm form;
   private final Artifact artifact;
   private final ArtifactEditor editor;
   private SelectionAdapter changeableAdapter;
   private SelectionAdapter nonChangeableAdapter;
   private Composite applicabilityComp;

   public ArtifactFormPageViewApplicability(ArtifactEditor editor, FormToolkit toolkit, ScrolledForm form) {
      this.editor = editor;
      this.artifact = editor.getArtifactFromEditorInput();
      this.toolkit = toolkit;
      this.form = form;
   }

   private void setButtonChangeable() {
      if (ViewApplicabilityUtil.isChangeApplicabilityValid(Collections.singleton(artifact))) {
         button.setText("Change");
         button.addSelectionListener(getChangeableAdapter());
      } else {
         button.setImage(getLockImage());
         button.addSelectionListener(getNonChangeableAdapter());
      }
   }

   private SelectionAdapter getChangeableAdapter() {
      if (changeableAdapter == null) {
         changeableAdapter = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               Pair<Boolean, String> result =
                  ViewApplicabilityUtil.changeApplicability(Collections.singletonList(artifact));
               boolean success = result.getFirst();
               if (success) {
                  if (editor.isDirty()) {
                     editor.doSave(null);
                  }
                  refresh();
               }
            }
         };
      }
      return changeableAdapter;
   }

   private SelectionAdapter getNonChangeableAdapter() {
      if (nonChangeableAdapter == null) {
         nonChangeableAdapter = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               AWorkbench.popup("Permission Denied", ViewApplicabilityUtil.CHANGE_APPLICABILITY_INVAILD);
            }
         };
      }
      return nonChangeableAdapter;
   }

   private Image getLockImage() {
      if (LOCK_IMAGE == null) {
         LOCK_IMAGE = ImageManager.getImage(FrameworkImage.LOCK_OVERLAY);
      }
      return LOCK_IMAGE;
   }

   public void create() {
      applicabilityComp = toolkit.createComposite(form.getForm().getBody(), SWT.WRAP);
      applicabilityComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      applicabilityComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      toolkit.adapt(applicabilityComp);

      text = toolkit.createFormText(applicabilityComp, false);
      text.setText(getArtifactViewApplicabiltyText(), true, false);
      text.setForeground(Displays.getSystemColor(SWT.COLOR_DARK_GRAY));
      toolkit.adapt(text);

      button = toolkit.createButton(applicabilityComp, "", SWT.PUSH);
      toolkit.adapt(button, true, true);
      setButtonChangeable();
   }

   public void refresh() {
      text.setText(getArtifactViewApplicabiltyText(), true, false);
      text.layout(true);
      applicabilityComp.layout(true);
   }

   private String getArtifactViewApplicabiltyText() {
      String result = "";
      ApplicabilityEndpoint applEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint(artifact.getBranch());
      if (applEndpoint == null) {
         result = "Error: Applicabilty Service not found";
      } else {
         try {
            result = applEndpoint.getApplicabilityToken(ArtifactId.create(artifact)).getName();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            result = "Error retrieving applicability. (see log)";
         }
      }
      return String.format("<form><p><b>View Applicability:</b> %s</p></form>", result);
   }
}