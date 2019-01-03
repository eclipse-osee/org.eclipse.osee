/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.agile;

import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class XOpenSprintSummaryButton extends AbstractXOpenSprintBurnupButton {

   public static final String WIDGET_ID = XOpenSprintSummaryButton.class.getSimpleName();

   public XOpenSprintSummaryButton() {
      super("Open Sprint Summary", "open.sprint.summary");
      setImage(ImageManager.getImage(AtsImage.REPORT));
      setToolTip("Click to open Sprint Summary Report");
      addXModifiedListener(listener);
   }

   @Override
   public String getUrl() {
      return System.getProperty(
         OseeClient.OSEE_APPLICATION_SERVER) + "/ats/agile/team/" + getAgileTeam().getIdString() + "/sprint/" + sprint.getIdString() + "/summary";
   }

}
