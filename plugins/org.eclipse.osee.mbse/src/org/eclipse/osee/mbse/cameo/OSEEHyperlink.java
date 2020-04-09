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

import com.nomagic.magicdraw.hyperlinks.Hyperlink;

/**
 * @author David W. Miller
 */
class OSEEHyperlink implements Hyperlink {
   public final static String PROTOCOL = "osee";
   private final String text;
   private final String url;

   public OSEEHyperlink(String text, String url) {
      this.text = text;
      this.url = url;
   }

   @Override
   public String getUrl() {
      return url;
   }

   @Override
   public String getText() {
      return text;
   }

   @Override
   public boolean isValid() {
      return true;
   }

   @Override
   public String getProtocol() {
      return PROTOCOL;
   }

   @Override
   public String getTypeText() {
      return "OSEE Link";
   }

   @Override
   public String toString() {
      return getUrl();
   }
}
