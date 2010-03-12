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
package org.eclipse.osee.framework.ui.swt;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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
      compositeMap = new HashMap<String, Control>();
      create();
   }

   private void create() {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      stackComposite = new Composite(this, SWT.NONE);
      stackLayout = new StackLayout();
      stackLayout.marginHeight = 0;
      stackLayout.marginWidth = 0;
      stackComposite.setLayout(stackLayout);
      stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      compositeMap.clear();
      compositeMap.put(DEFAULT_CONTROL, createDefault(stackComposite));

      setCurrentControl(DEFAULT_CONTROL);
   }

   public Control addControl(String key, Control control) {
      return compositeMap.put(key, control);
   }

   public Control removeControl(String key) {
      return compositeMap.remove(key);
   }

   public int getControlCount() {
      return compositeMap.size() - 1;
   }

   private Composite createDefault(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      Label label = new Label(composite, SWT.NONE);
      label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      label.setText("DEFAULT LAYER");
      label.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      return composite;
   }

   public Composite getStackComposite() {
      return stackComposite;
   }

   public void setCurrentControl(String key) {
      Control control = compositeMap.get(key);
      if (control == null) {
         control = compositeMap.get(DEFAULT_CONTROL);
      }
      stackLayout.topControl = control;
      stackComposite.layout();
   }

   @Override
   public void dispose() {
      for (Control control : compositeMap.values()) {
         Widgets.disposeWidget(control);
      }
      compositeMap.clear();
      Widgets.disposeWidget(stackComposite);
      super.dispose();
   }
}
