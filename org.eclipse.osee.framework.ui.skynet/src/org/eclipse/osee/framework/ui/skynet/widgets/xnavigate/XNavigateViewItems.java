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
package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class XNavigateViewItems {
   private static XNavigateViewItems navigateSearches = new XNavigateViewItems();

   protected XNavigateViewItems() {
      super();
   }

   public static XNavigateViewItems getInstance() {
      return navigateSearches;
   }

   public List<XNavigateItem> getSearchNavigateItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();
      return items;
   }

}
