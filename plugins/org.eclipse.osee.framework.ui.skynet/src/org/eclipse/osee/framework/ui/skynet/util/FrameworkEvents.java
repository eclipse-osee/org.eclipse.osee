/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.util;

/**
 * @author Donald G. Dunne
 */
public class FrameworkEvents {

   public static final String PERSONAL_WEB_PREFERENCES = "framework/web/prefs/modified/links/personal";
   public static final String GLOBAL_WEB_PREFERENCES = "framework/web/prefs/modified/links/global";
   public static final String FRAMEWORK_LINK_EDIT = "framework/link/edit";

   private FrameworkEvents() {
      // utility class
   }

}
