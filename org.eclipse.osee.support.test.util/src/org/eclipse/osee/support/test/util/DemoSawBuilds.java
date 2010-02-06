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
package org.eclipse.osee.support.test.util;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author Donald G. Dunne
 */
public class DemoSawBuilds extends NamedIdentity implements IOseeBranch {
   public static final DemoSawBuilds SAW_Bld_1 = new DemoSawBuilds("AyH_f2sSKy3l07fIvAAA", "SAW_Bld_1");
   public static final DemoSawBuilds SAW_Bld_2 = new DemoSawBuilds("AyH_f2sSKy3l07fIvBBB", "SAW_Bld_2");
   public static final DemoSawBuilds SAW_Bld_3 = new DemoSawBuilds("AyH_f2sSKy3l07fIvCCC", "SAW_Bld_3");

   private DemoSawBuilds(String guid, String name) {
      super(guid, name);
   }
}