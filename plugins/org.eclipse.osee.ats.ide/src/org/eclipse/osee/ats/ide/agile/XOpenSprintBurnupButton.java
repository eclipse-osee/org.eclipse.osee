/*********************************************************************
 * Copyright (c) 2017 Boeing
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
