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

package org.eclipse.osee.framework.ui.skynet.panels;

import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Ryan D. Brooks
 */
public class BrowserComposite extends Composite {
   private final Browser previewBrowser;
   private final ToolBar toolBar;

   public BrowserComposite(Composite parent, int style) {
      this(parent, style, null);
   }

   public BrowserComposite(Composite parent, int style, ToolBar toolBar) {
      super(parent, style);
      setLayout(ALayout.getZeroMarginLayout());
      GridData gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
      setLayoutData(gridData);

      previewBrowser = new Browser(this, SWT.NONE);
      gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
      previewBrowser.setLayoutData(gridData);

      addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            if (Widgets.isAccessible(previewBrowser)) {
               previewBrowser.dispose();
            }
         }
      });

      this.toolBar = toolBar;
   }

   public void setHtml(String html) throws SWTException, IllegalArgumentException {
      if (previewBrowser == null) {
         throw new RuntimeException("previewBrowser is null");
      }
      previewBrowser.setText(html);
   }

   public void setUrl(String url) throws SWTException, IllegalArgumentException {
      previewBrowser.setUrl(url);
   }

   public void refresh() {
      previewBrowser.refresh();
   }

   public boolean back() {
      if (previewBrowser == null) {
         return false;
      }
      return previewBrowser.back();
   }

   public boolean forward() {
      if (previewBrowser == null) {
         return false;
      }
      return previewBrowser.forward();
   }

   public boolean isBackEnabled() {
      if (previewBrowser == null) {
         return false;
      }
      return previewBrowser.isBackEnabled();
   }

   public boolean isForwardEnabled() {
      if (previewBrowser == null) {
         return false;
      }
      return previewBrowser.isForwardEnabled();
   }

   public void addProgressListener(ProgressListener listener) {
      previewBrowser.addProgressListener(listener);
   }

   /**
    * @return the toolBar
    */
   protected ToolBar getToolBar() {
      return toolBar;
   }

   protected String getUrl() {
      return previewBrowser.getUrl();
   }
}
