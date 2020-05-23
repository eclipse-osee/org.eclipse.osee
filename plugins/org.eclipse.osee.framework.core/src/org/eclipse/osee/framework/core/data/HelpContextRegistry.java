/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
