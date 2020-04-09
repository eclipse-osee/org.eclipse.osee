/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.mbse.cameo;

import com.nomagic.magicdraw.hyperlinks.HyperlinksHandlersRegistry;
import com.nomagic.magicdraw.plugins.Plugin;

/**
 * @author David W. Miller
 */
public class OSEEHyperlinkPlugin extends Plugin {
   @Override
   public void init() {
      HyperlinksHandlersRegistry.addHandler(new OSEEHyperlinkHandler());
   }

   @Override
   public boolean close() {
      return true;
   }

   @Override
   public boolean isSupported() {
      return true;
   }
}
