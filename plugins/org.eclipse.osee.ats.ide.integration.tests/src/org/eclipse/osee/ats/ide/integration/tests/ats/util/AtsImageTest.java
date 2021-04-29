/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import org.eclipse.osee.ats.api.util.AtsImage;

/**
 * @author Donald G. Dunne
 */
public class AtsImageTest extends AbstractImageManagerTest {

   public AtsImageTest() {
      super("AtsImage", AtsImage.getValues());
   }

}
