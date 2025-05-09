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
import org.eclipse.osee.ats.api.config.tx.IAtsProgramArtifactToken;
import org.eclipse.osee.ats.api.program.JaxProgram;

/**
 * @author Donald G. Dunne
 */
public class DemoProgram extends JaxProgram {

   private static List<DemoProgram> programs = new LinkedList<>();

   public static DemoProgram sawProgram =
      new DemoProgram(DemoCountry.DEMO_COUNTRY_US, DemoArtifactToken.SAW_Program, "SAW Program description");
   public static DemoProgram cisProgram =
      new DemoProgram(DemoCountry.DEMO_COUNTRY_US, DemoArtifactToken.CIS_Program, "CIS Program description");

   public static DemoProgram ver1 =
      new DemoProgram(DemoCountry.DEMO_COUNTRY_AJ, "Cntry V1", 888L, "CNTRY Ver1 Program description");
   public static DemoProgram ver2 =
      new DemoProgram(DemoCountry.DEMO_COUNTRY_AJ, "Cntry V2", 8881L, "CNTRY Ver2 Program description");
   public static DemoProgram ver3 =
      new DemoProgram(DemoCountry.DEMO_COUNTRY_AJ, "Cntry V3", 8882L, "CNTRY Ver3 Program description");

   List<DemoInsertion> insertions = new ArrayList<>();
   private final DemoCountry country;

   public DemoProgram(DemoCountry country, IAtsProgramArtifactToken program, String description) {
      this(country, program.getName(), program.getId(), description);
   }

   public DemoProgram(DemoCountry country, String name, long id, String description) {
      this.country = country;
      setName(name);
      setId(id);
      setDescription(description);
      setActive(true);
      this.insertions = new ArrayList<>();
      setCountryId(country.getId());
      programs.add(this);
   }

   public List<DemoInsertion> getInsertions() {
      return insertions;
   }

   public DemoCountry getCountry() {
      return country;
   }

   public static List<DemoProgram> getAllPrograms() {
      return programs;
   }

}
