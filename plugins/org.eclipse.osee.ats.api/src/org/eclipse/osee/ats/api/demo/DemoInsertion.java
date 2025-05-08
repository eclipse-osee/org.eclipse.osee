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
import org.eclipse.osee.ats.api.insertion.JaxInsertion;

/**
 * @author Donald G. Dunne
 */
public class DemoInsertion extends JaxInsertion {

   private static List<DemoInsertion> insertions = new ArrayList<>();

   public static DemoInsertion sawComm = new DemoInsertion(DemoProgram.sawProgram, "COMM", 23477771L, "COMM Insertion");
   public static DemoInsertion sawIdm =
      new DemoInsertion(DemoProgram.sawProgram, "IDM", 23477772L, "SAW IDM Insertion");
   public static DemoInsertion sawFixes =
      new DemoInsertion(DemoProgram.sawProgram, "Fixes", 23477773L, "Fixes for SAW");
   public static DemoInsertion sawTechApproach =
      new DemoInsertion(DemoProgram.sawProgram, "TA", 23477774L, "Tech Approaches for SAW");

   public static DemoInsertion cisTechApproach =
      new DemoInsertion(DemoProgram.cisProgram, "TA", 23477775L, "Tech Approaches for CIS");
   public static DemoInsertion cisAsdf = new DemoInsertion(DemoProgram.cisProgram, "ASDF", 23577776L, "ASDF Insertion");

   public static DemoInsertion ver1TechApproach = new DemoInsertion(DemoProgram.ver1, "TA", 23477777L, "TA Insertion");
   public static DemoInsertion ver1WetrPhase1 =
      new DemoInsertion(DemoProgram.ver1, "WETR Phase 1", 23577778L, "WETR Insertion");

   public static DemoInsertion ver2TechApproach =
      new DemoInsertion(DemoProgram.ver2, "TA", 23477779L, "WETR Insertion");
   public static DemoInsertion ver2WetrPhase1 =
      new DemoInsertion(DemoProgram.ver2, "WETR Phase 2", 23577770L, "WETR Phase 2 Insertion");

   public static DemoInsertion ver3TechApproach =
      new DemoInsertion(DemoProgram.ver3, "TA", 234777711L, "WETR Insertion");
   public static DemoInsertion ver3WetrPhase1 =
      new DemoInsertion(DemoProgram.ver3, "WETR Phase 3", 235777712L, "WETR Phase 3 Insertion");

   List<DemoInsertionActivity> activities = new ArrayList<>();

   private final DemoProgram program;

   public DemoInsertion(DemoProgram program, String name, long id, String description) {
      this.program = program;
      setName(name);
      setId(id);
      setActive(true);
      this.program.getInsertions().add(this);
      setDescription(description);
      setProgramId(program.getId());
      insertions.add(this);
   }

   public List<DemoInsertionActivity> getActivities() {
      return activities;
   }

   public DemoProgram getProgram() {
      return program;
   }

   public static List<DemoInsertion> getAllInsertions() {
      return insertions;
   }

}
