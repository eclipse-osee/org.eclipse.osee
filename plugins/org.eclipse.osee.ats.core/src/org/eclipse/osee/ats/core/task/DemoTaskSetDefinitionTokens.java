/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.task;

import org.eclipse.osee.ats.api.data.AtsTaskDefToken;

/**
 * @author Donald G. Dunne
 */
public class DemoTaskSetDefinitionTokens {

   public static AtsTaskDefToken SawSwDesignTestingChecklist =
      AtsTaskDefToken.valueOf(23492840234L, "Testing Checklist");
   public static AtsTaskDefToken SawSwDesignProcessChecklist =
      AtsTaskDefToken.valueOf(234965685392L, "Process Checklist");

}
