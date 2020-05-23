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

package org.eclipse.osee.framework.core.model.mocks;

import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;

/**
 * @author Roberto E. Escobar
 */
public final class MockAccessDetailCollector implements AccessDetailCollector {

   private AccessDetail<?> actualAccessDetail;

   public AccessDetail<?> getAccessDetails() {
      return actualAccessDetail;
   }

   public void clear() {
      collect(null);
   }

   @Override
   public void collect(AccessDetail<?> accessDetail) {
      this.actualAccessDetail = accessDetail;
   }
}