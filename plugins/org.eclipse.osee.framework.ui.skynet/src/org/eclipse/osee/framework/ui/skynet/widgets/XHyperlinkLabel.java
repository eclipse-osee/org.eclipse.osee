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

import java.net.URL;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkLabel extends GenericXWidget {

   private String url;
   private boolean openExternal;
   private Hyperlink hyperLinkLabel;
   private boolean addDefaultListener = true;

   public XHyperlinkLabel(String url) {
      this(url, url, false);
   }

   public XHyperlinkLabel(String label, String url, boolean openOutside) {
      super(label);
      this.url = url;
      this.openExternal = openOutside;
      setDisplayLabel(false);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout(1, false));
      GridData gd = new GridData();
      gd.horizontalSpan = horizontalSpan;
      comp.setLayoutData(gd);

      // Create Text Widgets
      if (toolkit == null) {
         hyperLinkLabel = new Hyperlink(comp, SWT.NONE);
      } else {
         hyperLinkLabel = toolkit.createHyperlink(comp, getLabel(), SWT.NONE);
      }
      hyperLinkLabel.setText(getLabel());
      hyperLinkLabel.setToolTipText(Strings.isValid(getToolTip()) ? getToolTip() : getUrl());
      hyperLinkLabel.setLayoutData(gd);
      if (isAddDefaultListener()) {
         hyperLinkLabel.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               handleSelection();
            }
         });
      }

      refresh();
   }

   public void handleSelection() {
      String useUrl = getUrl();
      if (!Strings.isValid(useUrl)) {
         AWorkbench.popup("No valid PCR to open");
      } else {
         if (openExternal) {
            Program.launch(useUrl);
         } else {
            IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
            try {
               IWebBrowser browser = browserSupport.createBrowser(null);
               browser.openURL(new URL(useUrl));
            } catch (Exception ex) {
               OseeLog.log(XHyperlinkLabel.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
   }

   @Override
   public Control getControl() {
      return hyperLinkLabel;
   }

   @Override
   public String toHTML(String labelFont) {
      return getUrl();
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public boolean isOpenExternal() {
      return openExternal;
   }

   public void setOpenExternal(boolean openExternal) {
      this.openExternal = openExternal;
   }

   public void setForgroundBlue() {
      hyperLinkLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
   }

   public boolean isAddDefaultListener() {
      return addDefaultListener;
   }

   public void setAddDefaultListener(boolean addDefaultListener) {
      this.addDefaultListener = addDefaultListener;
   }

}
