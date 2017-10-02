/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.country.IAtsCountry;
import org.eclipse.osee.ats.core.config.WorkPackageUtility;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link CountryColumn}
 * 
 * @author Donald G. Dunne
 */
public class CountryColumnTest extends CountryColumn {

   // @formatter:off
   @Mock private WorkPackageUtility util;
   @Mock private IAtsServices services;
   @Mock private IAtsWorkItem workItem;
   @Mock private IAtsObject object;
   @Mock private IAtsCountry country;
   // @formatter:on

   @Before
   public void setup()  {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void test() {
      String str = CountryColumn.getCountryStr(object, services, util);
      assertEquals(str, "");

      when(util.getCountry(services, workItem)).thenReturn(new Pair<IAtsCountry, Boolean>(null, false));

      str = CountryColumn.getCountryStr(workItem, services, util);
      assertEquals(str, "");

      when(util.getCountry(services, workItem)).thenReturn(new Pair<IAtsCountry, Boolean>(country, false));
      when(country.getName()).thenReturn("Country");
      str = CountryColumn.getCountryStr(workItem, services, util);
      assertEquals(str, "Country");

      when(util.getCountry(services, workItem)).thenReturn(new Pair<IAtsCountry, Boolean>(country, true));
      str = CountryColumn.getCountryStr(workItem, services, util);
      assertEquals(str, "Country (I)");

   }
}
