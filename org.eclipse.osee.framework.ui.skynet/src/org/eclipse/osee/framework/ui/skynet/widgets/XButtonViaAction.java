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
