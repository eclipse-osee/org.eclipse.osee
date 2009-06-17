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

import org.eclipse.osee.framework.skynet.core.ISkynetType;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;

public enum OTE_SKYNET_ARTIFACTS implements ISkynetType {
   TEST_RUN(Requirements.TEST_RUN), TEST_SCRIPT(Requirements.TEST_CASE);

   private String name;

   private OTE_SKYNET_ARTIFACTS(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}