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
package org.eclipse.osee.display.view.web.internal;

import org.eclipse.osee.vaadin.widgets.HasViews;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("serial")
public class OseeUiApplication extends Application {

   @Override
   public void init() {
      WindowFactory factory = new WindowFactory();
      HasViews viewProvider = new OseeUiViews();
      Window mainWindow = factory.createNavigatableWindow(viewProvider);
      setMainWindow(mainWindow);
      mainWindow.setApplication(this);
   }

   @Override
   public String getVersion() {
      Bundle bundle = FrameworkUtil.getBundle(this.getClass());
      return bundle.getVersion().toString();
   }

}
