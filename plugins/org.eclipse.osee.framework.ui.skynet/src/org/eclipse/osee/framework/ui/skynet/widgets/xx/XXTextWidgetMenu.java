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

package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Donald G. Dunne
 */
public class XXTextWidgetMenu {

   public static Menu getDefaultMenu(StyledText sText) {
      Menu menu = new Menu(sText.getShell());
      MenuItem cut = new MenuItem(menu, SWT.NONE);
      cut.setText("Cut");
      cut.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            sText.cut();
            sText.redraw();
         }
      });
      MenuItem copy = new MenuItem(menu, SWT.NONE);
      copy.setText("Copy");
      copy.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            sText.copy();
         }
      });
      MenuItem paste = new MenuItem(menu, SWT.NONE);
      paste.setText("Paste");
      paste.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            sText.paste();
            sText.redraw();
         }
      });
      return menu;
   }

}
