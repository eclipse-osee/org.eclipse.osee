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

package org.eclipse.osee.framework.ui.skynet.replace;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 * @author Wilik Karol
 */
public class ReplaceWithBaselineVersionDialog extends TitleAreaDialog {

   private static final String TITLE = "Replace with Baseline version";
   private boolean artifactsSelected;
   private boolean attributesSelected;
   private final boolean attrEnabled;
   private final boolean artEnabled;
   private final boolean relEnabled;

   public ReplaceWithBaselineVersionDialog(boolean artEnabled, boolean attrEnabled, boolean relEnabled) {
      super(Displays.getActiveShell());
      setDialogHelpAvailable(true);
      setShellStyle(SWT.SHELL_TRIM);
      setTitle(TITLE);
      this.attrEnabled = attrEnabled;
      this.artEnabled = artEnabled;
      this.relEnabled = relEnabled;
   }

   @Override
   protected void configureShell(Shell shell) {
      super.configureShell(shell);
      shell.setText(TITLE);
      PlatformUI.getWorkbench().getHelpSystem().setHelp(shell, OseeHelpContext.CHANGE_REPORT_EDITOR.asReference());
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Composite composite = new Composite(container, SWT.NONE);

      Button attribute = new Button(composite, SWT.RADIO);
      attribute.setText("Replace a single Attribute");
      attribute.addSelectionListener(attributeListener);
      attribute.setEnabled(attrEnabled);

      Button artifact = new Button(composite, SWT.RADIO);
      artifact.setText("Replace Artifact");
      artifact.setEnabled(attrEnabled || artEnabled);

      Button relation = new Button(composite, SWT.RADIO);
      relation.setText("Replace Relation");
      relation.setEnabled(relEnabled);

      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      return composite.getShell();
   }

   public boolean isArtifactSelected() {
      return artifactsSelected;
   }

   public boolean isAttributeSelected() {
      return attributesSelected;
   }

   public boolean isRelationSelected() {
      return relEnabled;
   }

   private final SelectionAdapter attributeListener = new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
         super.widgetSelected(e);
         attributesSelected = !attributesSelected;
      }
   };
}
