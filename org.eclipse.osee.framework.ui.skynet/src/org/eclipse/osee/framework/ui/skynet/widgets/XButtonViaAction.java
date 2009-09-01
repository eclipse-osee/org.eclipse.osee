/*
 * Created on Aug 31, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.jface.action.Action;

/**
 * @author Donald G. Dunne
 */
public class XButtonViaAction extends XButton {

   public XButtonViaAction(final Action action) {
      super(action.getText(), action.getImageDescriptor().createImage());
      if (action.getToolTipText() != null && !action.getToolTipText().equals("")) {
         setToolTip(action.getToolTipText());
      }
      addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            action.run();
         }
      });
   }

}
