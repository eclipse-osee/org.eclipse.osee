/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Donald G. Dunne
 */
public class ClientInfo {

   private static Pattern namePattern = Pattern.compile("Name: \\[(.*)\\]");
   private static Pattern versionPattern = Pattern.compile("Version:\\[(.*)\\]");
   private static Pattern userIdPattern = Pattern.compile("User Id:\\[(.*)\\]");
   private final String info;

   public ClientInfo(String info) {
      this.info = info;
   }

   public String getName() {
      return getValue(namePattern);
   }

   public String getVersion() {
      return getValue(versionPattern);
   }

   public String getUserId() {
      return getValue(userIdPattern);
   }

   public String getValue(Pattern pattern) {
      String name = "unknown";
      Matcher m = pattern.matcher(info);
      if (m.find()) {
         name = m.group(1);
      }
      return name;
   }
}
