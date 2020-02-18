/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

/**
 * @author Donald G. Dunne
 */
public class FrameworkEvents {

   public static final String PERSONAL_WEB_PREFERENCES = "framework/web/prefs/modified/links/personal";
   public static final String GLOBAL_WEB_PREFERENCES = "framework/web/prefs/modified/links/global";
   public static final String FRAMEWORK_LINK_EDIT = "framework/link/edit";

   public static final String NAVIGATE_VIEW_LOADED = "framework/navigator/loaded";

   private FrameworkEvents() {
      // utility class
   }

}
