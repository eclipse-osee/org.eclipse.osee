/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
