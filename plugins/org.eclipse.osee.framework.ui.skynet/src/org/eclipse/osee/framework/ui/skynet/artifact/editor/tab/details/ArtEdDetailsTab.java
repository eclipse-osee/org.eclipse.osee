/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.details;

import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.access.AccessControlDetails;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonViaAction;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class ArtEdDetailsTab extends FormPage {
   private ScrolledForm scrolledForm;
   public final static String ID = "art.editor.details.tab";
   private Browser browser;
   private final ArtifactEditor editor;
   private Composite bodyComp;
   private final Artifact artifact;
   private IManagedForm managedForm;

   public ArtEdDetailsTab(ArtifactEditor editor, Artifact artifact) {
      super(editor, ID, "Details");
      this.editor = editor;
      this.artifact = artifact;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      this.managedForm = managedForm;
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();

      bodyComp = scrolledForm.getBody();
      GridLayout gridLayout = new GridLayout(2, true);
      bodyComp.setLayout(gridLayout);
      GridData gd = new GridData(SWT.LEFT, SWT.LEFT, true, true);
      bodyComp.setLayoutData(gd);

      browser = new Browser(bodyComp, SWT.NONE);
      browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);

      createOperationsSection(bodyComp);
      refresh();
   }

   private void createOperationsSection(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      managedForm.getToolkit().adapt(composite);

      Label opsLabel = new Label(composite, SWT.BOLD);
      opsLabel.setFont(FontManager.getDefaultLabelFont());
      opsLabel.setText("Operations");
      opsLabel.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));

      XButtonViaAction button = new XButtonViaAction(new AccessControlDetails(artifact));
      button.setToolkit(managedForm.getToolkit());
      button.createWidgets(composite, 1);
      button.getLabelWidget().setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));

   }

   public void refresh() {
      if (Widgets.isAccessible(browser)) {

         try {
            Map<String, String> smaDetails = Artifacts.getDetailsKeyValues(artifact);
            FontData systemFont = browser.getDisplay().getSystemFont().getFontData()[0];
            String html = Artifacts.getDetailsFormText(smaDetails, systemFont.getName(), systemFont.getHeight());
            browser.setText(html);
         } catch (Exception ex) {
            browser.setText(Lib.exceptionToString(ex));
         }
         managedForm.reflow(true);
      }
   }

}
