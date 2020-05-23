/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.workdef;

/**
 * @author Donald G. Dunne
 */
public enum WfeHeader {

   WorkPackage(1),
   NoWorkPackage(1),
   Metrics(2),
   NoMetrics(2);

   private final int id;

   private WfeHeader(int id) {
      this.id = id;
   }

   public int getId() {
      return id;
   }

}
