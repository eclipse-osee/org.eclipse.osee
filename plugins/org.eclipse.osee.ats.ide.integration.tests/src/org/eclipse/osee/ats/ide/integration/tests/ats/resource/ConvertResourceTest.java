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
package org.eclipse.osee.ats.ide.integration.tests.ats.resource;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link ConvertResource}
 *
 * @author Donald G. Dunne
 */
public class ConvertResourceTest extends AbstractRestTest {

   @Test
   public void testGet() throws Exception {
      String results = getHtml("/ats/convert");
      Assert.assertTrue(results.contains("ATS - Conversions"));
   }
}