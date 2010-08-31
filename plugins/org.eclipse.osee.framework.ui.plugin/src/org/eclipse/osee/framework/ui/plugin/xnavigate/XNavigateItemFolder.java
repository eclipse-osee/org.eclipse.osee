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
package org.eclipse.osee.framework.ui.plugin.xnavigate;

import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItemFolder extends XNavigateItem {

   public XNavigateItemFolder(XNavigateItem parent, String name) throws OseeArgumentException {
      super(parent, name, name.contains("Admin") ? PluginUiImage.ADMIN : PluginUiImage.FOLDER);
   }

   public XNavigateItemFolder(XNavigateItem parent, String name, KeyedImage oseeImage) throws OseeArgumentException {
      super(parent, name, oseeImage);
   }

}