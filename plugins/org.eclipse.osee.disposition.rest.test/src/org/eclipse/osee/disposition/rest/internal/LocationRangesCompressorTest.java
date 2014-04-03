/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
      List<Integer> locationPoints = new ArrayList<Integer>();
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
   }
}
