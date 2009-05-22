/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.support.test.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Donald G. Dunne
 */
public enum DemoSubsystems {
   Robot_API, Robot_Survivability_Equipment, Robot_Systems_Management, Chasis, Cognitive_Decision_Aiding,
   //
   SAW_Product_Decomposition,
   Communications,
   Controls_and_Displays,
   Data_Management,
   Drive_Train,
   Electrical,
   Controls,
   //
   Hydraulics,
   Mission_Systems_Management,
   Navigation,
   Propulsion,
   Sights,
   Unknown,
   Unspecified;

   public static String[] getSubsystemArray() {
      return getSubsystems().toArray(new String[getSubsystems().size()]);
   }

   public static Collection<String> getSubsystems() {
      ArrayList<String> subsystems = new ArrayList<String>();
      for (DemoSubsystems subsystem : DemoSubsystems.values()) {
         subsystems.add(subsystem.name());
      }
      return subsystems;
   }
}
