/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.util;

/**
 * @author Donald G. Dunne
 */
public enum UpdateLocation {
   First,
   Last,
   Selection,
   AfterSelection;

   public boolean equals(String location) {
      return this.name().equals(location);
   }
}
