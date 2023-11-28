/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.util;

import org.eclipse.osee.ats.api.AtsApiSwaggerGenerator;
import org.eclipse.osee.define.rest.api.DefineApiSwaggerGenerator;
import org.eclipse.osee.disposition.rest.DispoSwaggerGenerator;
import org.eclipse.osee.mim.MimSwaggerGenerator;
import org.eclipse.osee.orcs.rest.OrcsSwaggerGenerator;

/**
 * @author Dominic Guss
 */
public class SwaggerGenerator {

   public static void main(String[] args) {
      DefineApiSwaggerGenerator.main(args);
      MimSwaggerGenerator.main(args);
      OrcsSwaggerGenerator.main(args);
      AtsApiSwaggerGenerator.main(args);
      DispoSwaggerGenerator.main(args);
   }
}
