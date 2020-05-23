/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.disposition.rest.internal;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Angel Avila
 */
public class LocationRangesCompressorTest {
   @Test
   public void testRangeCompressor() {
      List<Integer> locationPoints = new ArrayList<>();
      locationPoints.add(83);
      locationPoints.add(84);
      String result = LocationRangesCompressor.compress(locationPoints);
      Assert.assertEquals("83-84", result);

      locationPoints.add(1);
      locationPoints.add(3);
      locationPoints.add(4);
      locationPoints.add(21);
      locationPoints.add(2);
      result = LocationRangesCompressor.compress(locationPoints);
      Assert.assertEquals("1-4, 21, 83-84", result);

      locationPoints.add(89);
      result = LocationRangesCompressor.compress(locationPoints);
      Assert.assertEquals("1-4, 21, 83-84, 89", result);

      locationPoints.add(92);
      locationPoints.add(93);
      locationPoints.add(5);
      result = LocationRangesCompressor.compress(locationPoints);
      Assert.assertEquals("1-5, 21, 83-84, 89, 92-93", result);

      List<Integer> locationPointsZero = new ArrayList<>();
      locationPointsZero.add(0);
      result = LocationRangesCompressor.compress(locationPointsZero);
      Assert.assertEquals("0", result);
   }
}
