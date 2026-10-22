/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam.sections;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Collapsible "Details" section for the BLAM editor showing label/value pairs (BLAM name and code class name),
 * mirroring the Artifact Editor Details tab. Collapsed by default.
 *
 * @author Donald G. Dunne
 */
public class DetailsBlamSection extends BaseBlamSection {

   public DetailsBlamSection(BlamEditor editor, AbstractBlam abstractBlam, Composite parent, FormToolkit toolkit, int style) {
      super(editor, abstractBlam, parent, toolkit, style);
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      final FormToolkit toolkit = form.getToolkit();
      Section section = getSection();
      section.setText("BLAM Details");
      section.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

      Composite composite = toolkit.createComposite(section, SWT.BORDER);
      GridLayout layout = new GridLayout(2, false);
      layout.marginWidth = 5;
      layout.marginHeight = 5;
      layout.verticalSpacing = 4;
      layout.horizontalSpacing = 10;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

      AbstractBlam blam = getAbstractBlam();
      addDetailRow(toolkit, composite, "Name", blam.getName());
      addDetailRow(toolkit, composite, "Class", blam.getClass().getSimpleName());
      addDetailRow(toolkit, composite, "User Groups", toNames(blam.getUserGroups()));
      addDetailRow(toolkit, composite, "Categories", toNames(blam.getCategories()));

      section.setClient(composite);
      section.setExpanded(false);
      section.addExpansionListener(new ExpansionAdapter() {
         @Override
         public void expansionStateChanged(ExpansionEvent e) {
            getManagedForm().reflow(true);
         }
      });
   }

   private String toNames(Collection<? extends Named> namedItems) {
      return Collections.toString(", ", Named.getNames(namedItems));
   }

   private void addDetailRow(FormToolkit toolkit, Composite parent, String key, String value) {
      Label keyLabel = toolkit.createLabel(parent, key + ":", SWT.NONE);
      keyLabel.setFont(FontManager.getDefaultLabelFont());
      keyLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

      Text valueText = new Text(parent, SWT.READ_ONLY | SWT.SINGLE);
      valueText.setText(value == null ? "" : value);
      valueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      toolkit.adapt(valueText, true, true);
   }
}
