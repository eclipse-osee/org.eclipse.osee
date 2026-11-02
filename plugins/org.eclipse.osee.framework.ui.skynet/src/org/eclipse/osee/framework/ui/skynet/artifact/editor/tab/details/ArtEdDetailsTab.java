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

import java.util.Arrays;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.access.AccessControlDetails;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.header.ArtEdBaseTab;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXButtonViaActionWidget;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class ArtEdDetailsTab extends ArtEdBaseTab {

   public final static String ID = "art.editor.details.tab";
   private Composite detailsComp;
   private FormToolkit toolkit;

   public ArtEdDetailsTab(ArtifactEditor editor, Artifact artifact) {
      super(editor, ID, "Details");
   }

   @Override
   protected String getTabName() {
      return "Details";
   }

   @Override
   protected void createTabBody(Composite parent, FormToolkit toolkit) {
      this.toolkit = toolkit;

      GridLayout gridLayout = new GridLayout(2, true);
      gridLayout.marginWidth = 5;
      gridLayout.marginHeight = 5;
      parent.setLayout(gridLayout);

      // Left side: artifact details as XXLabelValueWidgets
      detailsComp = toolkit.createComposite(parent, SWT.NONE);
      GridLayout detailsLayout = new GridLayout(2, false);
      detailsLayout.marginWidth = 5;
      detailsLayout.marginHeight = 5;
      detailsLayout.verticalSpacing = 4;
      detailsLayout.horizontalSpacing = 10;
      detailsComp.setLayout(detailsLayout);
      detailsComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

      createDetailsWidgets();

      // Right side: operations
      createOperationsSection(parent);
   }

   private void createDetailsWidgets() {
      Artifact artifact = getArtifact();
      Map<String, String> details = Artifacts.getDetailsKeyValues(artifact);
      String[] keys = details.keySet().toArray(new String[0]);
      Arrays.sort(keys);

      for (String key : keys) {
         Label keyLabel = toolkit.createLabel(detailsComp, key + ":", SWT.NONE);
         keyLabel.setFont(FontManager.getDefaultLabelFont());
         keyLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

         org.eclipse.swt.widgets.Text valueText =
            new org.eclipse.swt.widgets.Text(detailsComp, SWT.READ_ONLY | SWT.SINGLE);
         valueText.setText(Strings.isValid(details.get(key)) ? details.get(key) : "");
         valueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
         toolkit.adapt(valueText, true, true);
      }
   }

   private void createOperationsSection(Composite parent) {
      Composite composite = toolkit.createComposite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

      Label opsLabel = toolkit.createLabel(composite, "Operations", SWT.NONE);
      opsLabel.setFont(FontManager.getDefaultLabelFont());

      XXButtonViaActionWidget accessButton = new XXButtonViaActionWidget(new AccessControlDetails(getArtifact()));
      accessButton.setToolkit(toolkit);
      accessButton.createWidgets(composite, 1);
      toolkit.adapt(accessButton.getLabelLinkWidget(), true, true);
   }

   @Override
   protected void refreshTabBody() {
      if (detailsComp != null && !detailsComp.isDisposed()) {
         for (org.eclipse.swt.widgets.Control child : detailsComp.getChildren()) {
            child.dispose();
         }
         createDetailsWidgets();
         detailsComp.layout(true);
         detailsComp.getParent().layout(true);
      }
   }
}
