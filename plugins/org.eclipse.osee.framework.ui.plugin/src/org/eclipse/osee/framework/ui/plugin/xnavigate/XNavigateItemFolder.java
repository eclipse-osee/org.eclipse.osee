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

import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItemFolder extends XNavigateItem {

   public XNavigateItemFolder(XNavigateItem parent, String name) {
      super(parent, name, name.contains("Admin") ? PluginUiImage.ADMIN : PluginUiImage.FOLDER);
   }

   public XNavigateItemFolder(XNavigateItem parent, String name, KeyedImage oseeImage) {
      super(parent, name, oseeImage);
   }

}