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
package org.eclipse.osee.framework.jini.service.test.interfaces;

import java.io.Serializable;

public class BuildTargetPair implements Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = 8194125694292612082L;
   String buildMachineName;
   String buildMachineIP;
   String targetMachineName;
   String targetMachineIP;

   public BuildTargetPair(String buildMachineName, String buildMachineIP, String targetMachineName, String targetMachineIP) {
      this.buildMachineName = buildMachineName;
      this.buildMachineIP = buildMachineIP;
      this.targetMachineName = targetMachineName;
      this.targetMachineIP = targetMachineIP;
   }

   public String getBuildMachineIP() {
      return buildMachineIP;
   }

   public String getBuildMachineName() {
      return buildMachineName;
   }

   public String getTargetMachineIP() {
      return targetMachineIP;
   }

   public String getTargetMachineName() {
      return targetMachineName;
   }

}
