/*********************************************************************
 * Copyright (c) 2016 Boeing
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
