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
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;

/**
 * @author Donald G. Dunne
 */
public class DemoInsertionActivity extends JaxInsertionActivity {

   private static List<DemoInsertionActivity> activities = new ArrayList<>();

   public static DemoInsertionActivity commPage =
      new DemoInsertionActivity(DemoInsertion.sawComm, "COMM Page", 23477781L, "description");
   public static DemoInsertionActivity commButton =
      new DemoInsertionActivity(DemoInsertion.sawComm, "COMM Button", 23477782L, "description");

   public static DemoInsertionActivity cisAsdf =
      new DemoInsertionActivity(DemoInsertion.cisAsdf, "cisAsdf Activity", 23477783L, "description");
   public static DemoInsertionActivity cisTechApproach =
      new DemoInsertionActivity(DemoInsertion.cisTechApproach, "cisAsdf TA Activity", 2347778L, "description");

   public static DemoInsertionActivity ver1TechApproachActivity =
      new DemoInsertionActivity(DemoInsertion.ver1TechApproach, 23477784L);
   public static DemoInsertionActivity ver1WetrPhase1 =
      new DemoInsertionActivity(DemoInsertion.ver1WetrPhase1, 23477785L);
   public static DemoInsertionActivity ver2TechApproach =
      new DemoInsertionActivity(DemoInsertion.ver2TechApproach, 23477786L);
   public static DemoInsertionActivity ver2WetrPhase1 =
      new DemoInsertionActivity(DemoInsertion.ver2WetrPhase1, 23477787L);
   public static DemoInsertionActivity ver3TechApproach =
      new DemoInsertionActivity(DemoInsertion.ver3TechApproach, 23477788L);
   public static DemoInsertionActivity ver3WetrPhase1 =
      new DemoInsertionActivity(DemoInsertion.ver3WetrPhase1, 23477789L);

   DemoInsertion insertion;

   public DemoInsertionActivity(DemoInsertion insertion, String name, long id, String description) {
      this.insertion = insertion;
      setName(name);
      setId(id);
      setActive(true);
      insertion.getActivities().add(this);
      setDescription(description);
      setInsertionId(insertion.getId());
      activities.add(this);
   }

   public DemoInsertionActivity(DemoInsertion insertion, long id) {
      this(insertion, insertion.getName() + " Activity", id, "description");
   }

   public static List<DemoInsertionActivity> getAllActivities() {
      return activities;
   }

   public DemoInsertion getInsertion() {
      return insertion;
   }

}
