/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.view.web.components;

import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Link;

@SuppressWarnings("serial")
public class OseeLogoLink extends Link {

   public OseeLogoLink(Navigator navigator, String styleName, Class<?> viewClass) {
      //super("", new ExternalResource(String.format("ats#%s", navigator.getUri(viewClass))));
      super("", new ExternalResource("ats"));
      Resource logoIconRes = new ThemeResource("../osee/osee_large.png");
      setIcon(logoIconRes);
      setStyleName(styleName);
   }
}
