/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.column.ev;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.core.column.IAtsColumnProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsColumnProviderCollector {

   private static List<IAtsColumnProvider> columnProviders;

   public AtsColumnProviderCollector() {
   }

   public void addColumnProvider(IAtsColumnProvider columnProvider) {
      getColumnProviders().add(columnProvider);
   }

   public static List<IAtsColumnProvider> getColumnProviders() {
      if (columnProviders == null) {
         columnProviders = new CopyOnWriteArrayList<>();
      }
      return columnProviders;
   }

}
