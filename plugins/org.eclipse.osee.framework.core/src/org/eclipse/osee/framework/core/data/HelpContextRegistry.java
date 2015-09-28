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
package org.eclipse.osee.framework.core.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Roberto E. Escobar
 */
public final class HelpContextRegistry {
   private static final Map<String, HelpContext> contexts = new ConcurrentHashMap<>();

   public static HelpContext asContext(String pluginId, String name) {
      String key = HelpContext.asReference(pluginId, name);
      HelpContext context = contexts.get(key);
      if (context == null) {
         context = new HelpContext(pluginId, name);
         contexts.put(context.asReference(), context);
      }
      return context;
   }
}
