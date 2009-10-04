/*
 * Created on Oct 4, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Donald G. Dunne
 */
public class ToolBarUtil {

   public static ToolItem actionToToolItem(ToolBar toolBar, int style, final Action action, final Image image) {
      return actionToToolItemWithText(toolBar, style, action, image, false);
   }

   public static ToolItem actionToToolItemWithText(ToolBar toolBar, int style, final Action action, final Image image, boolean withText) {
      ToolItem item = new ToolItem(toolBar, style);
      item.setImage(image);
      if (withText && action.getText() != null && !action.getText().equals("")) {
         item.setText(action.getText());
      }
      if (action.getText() != null && !action.getText().equals("")) {
         item.setToolTipText(action.getToolTipText());
      }
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            action.run();
         }
      });
      return item;
   }
}
