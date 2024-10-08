/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests;

import org.eclipse.osee.ats.ide.util.AtsApiIde;

/**
 * @author Donald G. Dunne
 */
public class AtsApiService {

   private static AtsApiIde atsApiIde;

   public void setAtsApiIde(AtsApiIde atsApiIde) {
      AtsApiService.atsApiIde = atsApiIde;
   }

   public static AtsApiIde get() {
      return atsApiIde;
   }

}