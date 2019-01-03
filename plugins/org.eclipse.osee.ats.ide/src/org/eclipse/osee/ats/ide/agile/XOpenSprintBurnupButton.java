/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.agile;

import org.eclipse.osee.framework.core.data.OseeClient;

/**
 * @author Donald G. Dunne
 */
public class XOpenSprintBurnupButton extends AbstractXOpenSprintBurnupButton {

   public static final String WIDGET_ID = XOpenSprintBurnupButton.class.getSimpleName();

   public XOpenSprintBurnupButton() {
      super("Open Sprint Burn-Up", "open.burnup");
   }

   @Override
   public String getUrl() {
      return System.getProperty(
         OseeClient.OSEE_APPLICATION_SERVER) + "/ats/agile/team/" + getAgileTeam().getIdString() + "/sprint/" + sprint.getIdString() + "/burnup/chart/ui";
   }

}
