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

import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItemFolder extends XNavigateItem {

   /**
    * @param parent
    * @param name
    * @throws OseeArgumentException
    */
   public XNavigateItemFolder(XNavigateItem parent, String name) throws OseeArgumentException {
      super(parent, name, name.contains("Admin") ? FrameworkImage.ADMIN : FrameworkImage.FOLDER);
   }

   /**
    * @param parent
    * @param name
    * @param image
    * @throws OseeArgumentException
    */
   public XNavigateItemFolder(XNavigateItem parent, String name, OseeImage oseeImage) throws OseeArgumentException {
      super(parent, name, oseeImage);
   }
}