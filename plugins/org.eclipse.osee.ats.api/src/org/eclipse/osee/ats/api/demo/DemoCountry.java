/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.demo;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.country.JaxCountry;

/**
 * @author Donald G. Dunne
 */
public class DemoCountry extends JaxCountry {

   public static DemoCountry DEMO_COUNTRY_US =
      new DemoCountry("DEMO_COUNTRY_US", 77771L, "ATS Configuration is support of DEMO Govt");
   public static DemoCountry DEMO_COUNTRY_AJ =
      new DemoCountry("DEMO_COUNTRY_A", 77772L, "ATS Configuration is support of Country");

   private final List<DemoProgram> programs;

   public DemoCountry(String name, long id, String description) {
      setName(name);
      setId(id);
      setDescription(description);
      setActive(true);
      this.programs = new ArrayList<>();
   }

   public List<DemoProgram> getPrograms() {
      return programs;
   }

}
