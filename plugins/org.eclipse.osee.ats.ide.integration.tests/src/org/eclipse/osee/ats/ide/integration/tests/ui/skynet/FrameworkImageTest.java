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

package org.eclipse.osee.ats.ide.integration.tests.ui.skynet;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class FrameworkImageTest extends ImageManagerTest {

   public FrameworkImageTest() {
      super("FrameworkImage", FrameworkImage.values());
   }

   /**
    * Bug in Eclipse where some images can't be loaded during test
    */
   @Override
   protected Collection<KeyedImage> getSkipOseeImages() {
      return Arrays.asList(FrameworkImage.DELTAS_BASE_TO_HEAD_TXS, FrameworkImage.DELTAS_DIFFERENT_BRANCHES_WITH_MERGE);
   }

}
