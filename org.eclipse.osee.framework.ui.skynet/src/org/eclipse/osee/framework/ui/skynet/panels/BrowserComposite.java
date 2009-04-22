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

package org.eclipse.osee.framework.ui.skynet.panels;

import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Ryan D. Brooks
 */
public class BrowserComposite extends Composite {
   private Browser previewBrowser;
   private ToolBar toolBar;

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

      this.toolBar = toolBar;
   }

   public void setHtml(String html) throws SWTException, IllegalArgumentException {
      previewBrowser.setText(html);
   }

   public void setUrl(String url) throws SWTException, IllegalArgumentException {
      previewBrowser.setUrl(url);
   }

   public void refresh() {
      previewBrowser.refresh();
   }

   public boolean back() {
      if (previewBrowser == null) return false;
      return previewBrowser.back();
   }

   public boolean forward() {
      if (previewBrowser == null) return false;
      return previewBrowser.forward();
   }

   public boolean isBackEnabled() {
      if (previewBrowser == null) return false;
      return previewBrowser.isBackEnabled();
   }

   public boolean isForwardEnabled() {
      if (previewBrowser == null) return false;
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
