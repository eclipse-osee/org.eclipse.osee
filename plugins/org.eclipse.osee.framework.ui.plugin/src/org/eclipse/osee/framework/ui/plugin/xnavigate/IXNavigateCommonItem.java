/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.util.List;

/**
 * Implement to contribute items to common navigate views.
 *
 * @author Donald G. Dunne
 */
public interface IXNavigateCommonItem {

   public String getSectionId();

   default public void createCommonSection(List<XNavigateItem> items, List<String> excludeSectionIds) {
      // do nothing
   }

   default public void addUtilItems(XNavigateItem utilItems) {
      // do nothing
   }

}
