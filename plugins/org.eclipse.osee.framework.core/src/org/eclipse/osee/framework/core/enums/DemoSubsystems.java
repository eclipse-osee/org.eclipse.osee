/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.enums;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Donald G. Dunne
 */
public enum DemoSubsystems {
   Robot_API,
   Robot_Survivability_Equipment,
   Robot_Systems_Management,
   Chassis,
   Cognitive_Decision_Aiding,
   Communications,
   Data_Management,
   Electrical,
   Controls,
   Hydraulics,
   Navigation,
   Propulsion,
   Unknown,
   Unspecified;

   public static String[] getSubsystemArray() {
      return getSubsystems().toArray(new String[getSubsystems().size()]);
   }

   public static Collection<String> getSubsystems() {
      ArrayList<String> subsystems = new ArrayList<>();
      for (DemoSubsystems subsystem : DemoSubsystems.values()) {
         subsystems.add(subsystem.name());
      }
      return subsystems;
   }
}
