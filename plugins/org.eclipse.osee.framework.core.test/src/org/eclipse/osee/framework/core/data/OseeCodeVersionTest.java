/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for @link OseeCodeVersion
 *
 * @author Donald G. Dunne
 */
public class OseeCodeVersionTest {

   @Test
   public void test() {
      OseeCodeVersion.setVersion("RC_Development");
      OseeCodeVersion.setBundleVersion("0.25.2.v201801051909-NR");

      // NR-Alpha = 3
      Assert.assertEquals(25022018010519093L, OseeCodeVersion.computeVersionId().longValue());

      // NR-Beta = 2
      OseeCodeVersion.setVersion(OseeCodeVersion.getBundleVersion());
      Assert.assertEquals(25022018010519092L, OseeCodeVersion.computeVersionId().longValue());

      // Release = 1
      OseeCodeVersion.setBundleVersion("0.25.2.v201801051909-REL");
      OseeCodeVersion.setVersion(OseeCodeVersion.getBundleVersion());
      Assert.assertEquals(25022018010519091L, OseeCodeVersion.computeVersionId().longValue());

      // DEV-Alpha = 5
      OseeCodeVersion.setBundleVersion("0.25.2.v201801051909-DEV");
      OseeCodeVersion.setVersion("DEVELOPMENT");
      Assert.assertEquals(25022018010519095L, OseeCodeVersion.computeVersionId().longValue());

      // DEV-Beta = 4
      OseeCodeVersion.setVersion(OseeCodeVersion.getBundleVersion());
      Assert.assertEquals(25022018010519094L, OseeCodeVersion.computeVersionId().longValue());

      // Unknown = 0s
      OseeCodeVersion.setBundleVersion("0.25.2.v201801051909-XYZ");
      Assert.assertEquals(25022018010519090L, OseeCodeVersion.computeVersionId().longValue());

      // Invalid build pattern = 0
      OseeCodeVersion.setBundleVersion("025.2v201801051909-DEV");
      Assert.assertEquals(0L, OseeCodeVersion.computeVersionId().longValue());
   }

}
