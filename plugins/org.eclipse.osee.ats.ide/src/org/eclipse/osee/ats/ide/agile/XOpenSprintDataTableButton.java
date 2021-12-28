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

import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class XOpenSprintDataTableButton extends AbstractXOpenSprintBurnupButton {

   public XOpenSprintDataTableButton() {
      super("Open Sprint Data Table", "open.sprint.data");
      setImage(ImageManager.getImage(AtsImage.REPORT));
      setToolTip("Click to open Sprint Data Table");
      addXModifiedListener(listener);
   }

   @Override
   public String getUrl() {
      return System.getProperty(
         OseeClient.OSEE_APPLICATION_SERVER) + "/ats/agile/team/" + getAgileTeam().getIdString() + "/sprint/" + sprint.getIdString() + "/data/table";
   }

}
