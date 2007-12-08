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
package org.eclipse.osee.framework.ui.service.control.widgets;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.ui.service.control.renderer.IRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Roberto E. Escobar
 */
public class StackedViewer extends Composite {

   public static final String DEFAULT_CONTROL = "DEFAULT_CONTROL";
   private StackLayout stackLayout;
   private Composite stackComposite;
   private Map<String, Control> compositeMap;

   public StackedViewer(Composite parent, int style) {
      super(parent, style);
      create();
   }

   private void create() {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      stackComposite = new Composite(this, SWT.NONE);
      stackLayout = new StackLayout();
      stackComposite.setLayout(stackLayout);
      stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      populateStackedComposite(stackComposite);
      displayArea(DEFAULT_CONTROL);
   }

   private void populateStackedComposite(Composite parent) {
      compositeMap = new HashMap<String, Control>();
      compositeMap.put(DEFAULT_CONTROL, createDefault(parent));
   }

   public void addControl(String key, IRenderer renderer) {
      compositeMap.put(key, renderer.renderInComposite(stackComposite));
   }

   private Composite createDefault(Composite parent) {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      label.setText("DEFAULT LAYER");
      return label.getShell();
   }

   public void displayArea(String key) {
      stackLayout.topControl = compositeMap.get(key);
      stackComposite.layout();
   }
}
