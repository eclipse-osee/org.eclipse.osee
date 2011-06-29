/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.test.util.enumeration;

import java.util.Set;
import org.eclipse.osee.framework.ui.skynet.util.enumeration.AbstractEnumeration;

public class OneEnum extends AbstractEnumeration {

   public static OneEnum Endorse = new OneEnum("Endorse", "This is OneStates Endorse");
   public static OneEnum Cancelled = new OneEnum("Cancelled");
   public static OneEnum Completed = new OneEnum("Completed");

   public OneEnum(String pageName) {
      super(OneEnum.class, pageName);
   }

   public OneEnum(String pageName, String description) {
      super(OneEnum.class, pageName);
      setDescription(description);
   }

   public static OneEnum valueOf(String pageName) {
      return AbstractEnumeration.valueOfPage(OneEnum.class, pageName);
   }

   public static Set<OneEnum> values() {
      return AbstractEnumeration.pages(OneEnum.class);
   }

}
