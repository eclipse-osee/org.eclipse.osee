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
package org.eclipse.osee.ote.define.AUTOGEN;

import org.eclipse.osee.framework.skynet.core.IOseeType;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;

public enum OteArtifacts implements IOseeType {
   TEST_RUN(Requirements.TEST_RUN, "AAMFDjqDHWo+orlSpaQA"), TEST_SCRIPT(Requirements.TEST_CASE, "AAMFDikEi0TGK27TKPgA");

   private final String name;
   private final String guid;

   private OteArtifacts(String name, String guid) {
      this.name = name;
      this.guid = guid;
   }

   public String getName() {
      return this.name;
   }

   public String getGuid() {
      return guid;
   }
}