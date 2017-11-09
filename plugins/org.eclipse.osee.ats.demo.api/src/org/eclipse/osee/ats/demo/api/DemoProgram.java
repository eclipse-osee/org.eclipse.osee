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
package org.eclipse.osee.ats.demo.api;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.program.JaxProgram;

/**
 * @author Donald G. Dunne
 */
public class DemoProgram extends JaxProgram {

   public static DemoProgram sawProgram =
      new DemoProgram(DemoCountry.usg, "SAW Program", 19196003L, "SAW Program description");
   public static DemoProgram cisProgram =
      new DemoProgram(DemoCountry.usg, "CIS Program", 8242414L, "CIS Program description");

   public static DemoProgram ver1 =
      new DemoProgram(DemoCountry.cntry, "Cntry V1", 888L, "CNTRY Ver1 Program description");
   public static DemoProgram ver2 =
      new DemoProgram(DemoCountry.cntry, "Cntry V2", 8881L, "CNTRY Ver2 Program description");
   public static DemoProgram ver3 =
      new DemoProgram(DemoCountry.cntry, "Cntry V3", 8882L, "CNTRY Ver3 Program description");

   List<DemoInsertion> insertions;
   private final DemoCountry country;
   private static List<DemoProgram> programs;

   public DemoProgram(DemoCountry country, String name, long id, String description) {
      this.country = country;
      setName(name);
      setId(id);
      setDescription(description);
      setActive(true);
      this.insertions = new ArrayList<>();
      country.getPrograms().add(this);
      setCountryId(country.getId());
      if (programs == null) {
         programs = new LinkedList<>();
      }
      programs.add(this);
   }

   public List<DemoInsertion> getInsertions() {
      return insertions;
   }

   public DemoCountry getCountry() {
      return country;
   }

   public static List<DemoProgram> getPrograms() {
      return programs;
   }

}
