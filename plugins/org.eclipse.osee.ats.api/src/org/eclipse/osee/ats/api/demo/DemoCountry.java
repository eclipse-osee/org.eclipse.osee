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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.country.JaxCountry;

/**
 * @author Donald G. Dunne
 */
public class DemoCountry extends JaxCountry {

   public static DemoCountry usg = new DemoCountry("USG", 7777L, "ATS Configuration is support of US Govt");

   public static DemoCountry cntry = new DemoCountry("CNTRY", 77771L, "ATS Configuration is support of Country");

   List<DemoProgram> programs;
   private static List<DemoCountry> countries;

   public DemoCountry(String name, long id, String description) {
      setName(name);
      setId(id);
      setDescription(description);
      setActive(true);
      this.programs = new ArrayList<>();
      for (DemoProgram prog : programs) {
         this.programs.add(prog);
      }
      if (countries == null) {
         countries = new LinkedList<>();
      }
      countries.add(this);
   }

   public List<DemoProgram> getPrograms() {
      return programs;
   }

   public static List<DemoCountry> getCountries() {
      return countries;
   }

}
