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
package org.eclipse.osee.ote.ui.define.panels;

import java.net.URI;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class PreviewPanel extends Composite {
   private static final String PREVIEW_DEFAULT_TITLE = "Preview Not Available";

   public enum PanelEnum {
      DEFAULT, BROWSER;
   }

   private Composite stackedComposite;
   private StackLayout stackLayout;
   private Composite defaultComposite;
   private Browser browser;

   public PreviewPanel(Composite parent, int style) {
      super(parent, style);
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      this.setLayout(layout);
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      createControl(this);
   }

   private void createControl(Composite parent) {
      stackedComposite = new Composite(parent, SWT.BORDER);
      stackLayout = new StackLayout();
      stackLayout.marginWidth = 0;
      stackLayout.marginHeight = 0;
      stackedComposite.setLayout(stackLayout);
      stackedComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createBrowserArea(stackedComposite);
      createPreviewNotAllowed(stackedComposite);
   }

   private void createBrowserArea(Composite parent) {
      browser = new Browser(parent, SWT.EMBEDDED | SWT.BORDER);
      browser.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
      browser.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
   }

   private void createPreviewNotAllowed(Composite parent) {
      defaultComposite = new Composite(parent, SWT.BORDER);
      defaultComposite.setLayout(new GridLayout());
      defaultComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      defaultComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      Label label = new Label(defaultComposite, SWT.NONE);
      Font font = new Font(PlatformUI.getWorkbench().getDisplay(), "Courier New", 10, SWT.BOLD);
      label.setFont(font);
      label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
      label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
      label.setText(PREVIEW_DEFAULT_TITLE);
      label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
   }

   private Control getControl(PanelEnum panelId) {
      Control control = defaultComposite;
      if (panelId.equals(PanelEnum.BROWSER)) {
         control = browser;
      }
      return control;
   }

   public void setDisplay(final PanelEnum panelId) {
      Control control = getControl(panelId);
      stackLayout.topControl = control;
      stackedComposite.layout();
      stackedComposite.getParent().layout();
      getParent().layout();
      getParent().getParent().layout();
   }

   public void updatePreview(final PanelEnum panelId, final URI uri) {
      try {
         if (panelId.equals(PanelEnum.BROWSER)) {
            browser.setUrl(uri.toURL().toString());
         }
      } catch (Exception ex) {
         OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, String.format("Error Updating Preview [%s]", panelId), ex);
      }
   }
}
