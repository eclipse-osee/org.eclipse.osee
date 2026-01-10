/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.rest.internal.report;

import java.util.ArrayList;
import java.util.List;

public class RestData {

   public List<ActualUrl> actuals = new ArrayList<>();
   public List<ExpectedUrl> expected = new ArrayList<>();

   public RestData() {
   }

   public void addActual(ActualUrl actual) {
      actuals.add(actual);
   }

   public void addExpected(ExpectedUrl expected) {
      if (!this.expected.contains(expected)) {
         this.expected.add(expected);
      }
   }

}
