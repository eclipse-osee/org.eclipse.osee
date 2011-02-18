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
package org.eclipse.osee.ats.access;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public final class AtsAccessContextIdFactory {

   private static final Map<String, AccessContextId> guidToIds = new ConcurrentHashMap<String, AccessContextId>();

   private AtsAccessContextIdFactory() {
      // Static Factory Class
   }

   public static AccessContextId createContextId(final String guid, final String name) {
      AccessContextId context = guidToIds.get(guid);
      if (context == null) {
         context = TokenFactory.createAccessContextId(guid, name);
         guidToIds.put(guid, context);
      } else {
         OseeLog.log(
            AtsPlugin.class,
            Level.SEVERE,
            String.format("Duplicate AtsAccessContextIds with guid [%s] named [%s] and [%s]", guid, name,
               context.getName()));
      }
      return context;
   }

   public static AccessContextId getOrCreate(final String guid) {
      AccessContextId context = guidToIds.get(guid);
      if (context == null) {
         context = createContextId(guid, "name unknown");
      }
      return context;
   }
}
