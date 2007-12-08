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
package org.eclipse.osee.framework.jini.service.core;

public class VersionEntry extends FormmatedEntry {

   private static final long serialVersionUID = 132189087526085874L;
   public String version;

   public VersionEntry() {
      version = "1.0.0";
   }

   public String getFormmatedString() {
      return "Version: " + version;
   }

   public boolean equals(Object other) {
      if (!(other instanceof VersionEntry)) return false;
      return version.equals(((VersionEntry) other).version);
   }

}
