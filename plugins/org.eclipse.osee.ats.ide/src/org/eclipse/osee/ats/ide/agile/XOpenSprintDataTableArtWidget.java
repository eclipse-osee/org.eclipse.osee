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
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XOpenSprintDataTableArtWidget extends XAbstractOpenSprintBurnupArtWidget {

   public static WidgetId ID = WidgetIdAts.XOpenSprintDataTableArtWidget;

   public XOpenSprintDataTableArtWidget() {
      super(ID, "Open Sprint Data Table", "open.sprint.data");
      setOseeImage(AtsImage.REPORT);
      setToolTip("Click to open Sprint Data Table");
      addXModifiedListener(listener);
   }

   @Override
   public String getUrl() {
      return System.getProperty(
         OseeClient.OSEE_APPLICATION_SERVER) + "/ats/agile/team/" + getAgileTeam().getIdString() + "/sprint/" + sprint.getIdString() + "/data/table";
   }

}
