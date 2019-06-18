/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class FormsUtil {

   public static Pair<Label, Text> createLabelText(XFormToolkit toolkit, Composite comp, String labelStr, String valueStr) {
      return createLabelText(toolkit, comp, labelStr, valueStr, null);
   }

   public static void createLabelOrHyperlink(Composite comp, XFormToolkit toolkit, final int horizontalSpan, final String str) {
      if (str.length() > 150) {
         Hyperlink label = toolkit.createHyperlink(comp, Strings.truncate(str, 150) + "...", SWT.NONE);
         label.setToolTipText("click to view all");
         label.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               new HtmlDialog("Note", null, str).open();
            }
         });
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalSpan = horizontalSpan;
         label.setLayoutData(gd);
      } else {
         Label label = toolkit.createLabel(comp, str);
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalSpan = horizontalSpan;
         label.setLayoutData(gd);
      }
   }

   public static Pair<Label, Text> createLabelText(XFormToolkit toolkit, Composite comp, String labelStr, String valueStr, String tooltip) {
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

   public static Label createLabelValue(XFormToolkit toolkit, Composite comp, String labelStr, String valueStr, String tooltip) {
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

   public static void addHeadingGradient(FormToolkit toolkit, ScrolledForm form, boolean add) {
      FormColors colors = toolkit.getColors();
      Color top = colors.getColor(IFormColors.H_GRADIENT_END);
      Color bot = colors.getColor(IFormColors.H_GRADIENT_START);
      if (add) {
         form.getForm().setTextBackground(new Color[] {top, bot}, new int[] {100}, true);
      } else {
         form.getForm().setTextBackground(null, null, false);
         form.getForm().setBackground(colors.getBackground());
      }
      form.getForm().setHeadColor(IFormColors.H_BOTTOM_KEYLINE1,
         add ? colors.getColor(IFormColors.H_BOTTOM_KEYLINE1) : null);
      form.getForm().setHeadColor(IFormColors.H_BOTTOM_KEYLINE2,
         add ? colors.getColor(IFormColors.H_BOTTOM_KEYLINE2) : null);
      form.getForm().setHeadColor(IFormColors.H_HOVER_LIGHT, add ? colors.getColor(IFormColors.H_HOVER_LIGHT) : null);
      form.getForm().setHeadColor(IFormColors.H_HOVER_FULL, add ? colors.getColor(IFormColors.H_HOVER_FULL) : null);
      form.getForm().setHeadColor(IFormColors.TB_TOGGLE, add ? colors.getColor(IFormColors.TB_TOGGLE) : null);
      form.getForm().setHeadColor(IFormColors.TB_TOGGLE_HOVER,
         add ? colors.getColor(IFormColors.TB_TOGGLE_HOVER) : null);
      form.getForm().setSeparatorVisible(add);
      form.reflow(true);
      form.redraw();
   }

}
