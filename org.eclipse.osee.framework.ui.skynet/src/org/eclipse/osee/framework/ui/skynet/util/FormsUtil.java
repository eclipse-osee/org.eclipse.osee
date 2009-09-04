/*
 * Created on Sep 4, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class FormsUtil {

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
