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

package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class FormsUtil {

   public static Pair<Label, Text> createLabelText(XFormToolkit toolkit, Composite comp, String labelStr,
      String valueStr) {
      return createLabelText(toolkit, comp, labelStr, valueStr, null);
   }

   public static Pair<Label, Text> createLabelText(XFormToolkit toolkit, Composite comp, String labelStr,
      String valueStr, String tooltip) {
      Composite topLineComp = new Composite(comp, SWT.NONE);
      topLineComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      topLineComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      toolkit.adapt(topLineComp);

      Label label = toolkit.createLabel(topLineComp, labelStr);
      if (Strings.isValid(tooltip)) {
         label.setToolTipText(tooltip);
      }
      setLabelFonts(label, FontManager.getDefaultLabelFont());
      Text text = new Text(topLineComp, SWT.NO_TRIM);
      text.setLayoutData(new GridData());
      toolkit.adapt(text, true, true);
      text.setText(valueStr);
      if (Strings.isValid(tooltip)) {
         text.setToolTipText(tooltip);
      }
      return new Pair<Label, Text>(label, text);
   }

   public static Label createLabelValue(XFormToolkit toolkit, Composite comp, String labelStr, String valueStr) {
      return createLabelValue(toolkit, comp, labelStr, valueStr, null);
   }

   public static Label createLabelValue(XFormToolkit toolkit, Composite comp, String labelStr, String valueStr,
      String tooltip) {
      Composite topLineComp = new Composite(comp, SWT.NONE);
      topLineComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      topLineComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      toolkit.adapt(topLineComp);
      Label label = toolkit.createLabel(topLineComp, labelStr, SWT.NONE);
      label.setLayoutData(new GridData());
      if (Strings.isValid(tooltip)) {
         label.setToolTipText(tooltip);
      }
      setLabelFonts(label, FontManager.getDefaultLabelFont());
      Label valueLabel = toolkit.createLabel(topLineComp, valueStr, SWT.NONE);
      if (Strings.isValid(tooltip)) {
         valueLabel.setToolTipText(tooltip);
      }
      valueLabel.setLayoutData(new GridData());
      return valueLabel;
   }

   public static void setLabelFonts(Control parent, Font font) {
      if (parent instanceof Label) {
         Label label = (Label) parent;
         label.setFont(font);
      }
      if (parent instanceof Composite) {
         Composite container = (Composite) parent;
         for (Control child : container.getChildren()) {
            setLabelFonts(child, font);
         }
         container.layout();
      }
   }

}
