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

package org.eclipse.osee.framework.ui.skynet.markedit.html;

import org.eclipse.osee.framework.ui.skynet.markedit.model.AbstractOmeData;
import org.eclipse.osee.framework.ui.skynet.markedit.model.ArtOmeData;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class OmeHtmlComposite extends Composite {

   protected Browser browser;

   public OmeHtmlComposite(Composite parent, int style, AbstractOmeData omeData) {
      super(parent, style);

      setLayout(ALayout.getZeroMarginLayout());
      setLayoutData(new GridData(GridData.FILL_BOTH));

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));

      try {
         Button reload = new Button(this, SWT.PUSH);
         reload.setText("Refresh");
         reload.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               handleRefreshAction(omeData);
            }

         });
         browser = new Browser(this, SWT.BORDER);
         if (omeData instanceof ArtOmeData) {
            browser.addLocationListener(new OmeBrowserListener((ArtOmeData) omeData));
         }
         browser.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
         OmeBrowserMenu menu = new OmeBrowserMenu(omeData, browser);
         browser.setMenu(menu.getPopup(parent));
      } catch (SWTError e) {
         // do nothing
      }

      addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            if (Widgets.isAccessible(browser)) {
               browser.dispose();
            }
         }
      });

   }

   public void handleRefreshAction(AbstractOmeData omeData) {
      OmeHtmlTab.handleRefreshAction(omeData, browser, null);
   }

   public Browser getBrowser() {
      return browser;
   }

}
