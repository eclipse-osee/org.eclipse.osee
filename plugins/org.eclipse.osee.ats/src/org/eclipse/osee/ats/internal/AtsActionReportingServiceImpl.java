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
package org.eclipse.osee.ats.internal;

import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.plugin.core.IActionReportingService;

/**
 * @author Roberto E. Escobar
 */
public class AtsActionReportingServiceImpl implements IActionReportingService {

   @Override
   public void report(String actionableItem, String desc) throws Exception {
      AtsUtil.createATSAction(desc, actionableItem);
   }
}
