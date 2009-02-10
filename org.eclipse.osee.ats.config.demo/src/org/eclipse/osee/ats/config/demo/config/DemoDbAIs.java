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
package org.eclipse.osee.ats.config.demo.config;

/**
 * @author Donald G. Dunne
 */
public enum DemoDbAIs {
   Computers,
   Network,
   Config_Mgmt,
   Reviews,
   Timesheet,
   Website,
   Reader,
   CIS_Code,
   CIS_Test,
   CIS_Requirements,
   CIS_SW_Design,
   SAW_Code,
   SAW_Test,
   SAW_Requirements,
   SAW_SW_Design,
   Adapter;

   public String getAIName() {
      return name().replaceAll("_", " ");
   }
}
