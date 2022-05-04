/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.agile.navigate;

import static org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat.SUBCAT;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.core.agile.AgileUtil;
import org.eclipse.osee.ats.ide.AtsArtifactImageProvider;
import org.eclipse.osee.ats.ide.actions.SprintReportAction;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.navigate.ConvertVersionToAgileSprint;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemFolder;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;

/**
 * @author Donald G. Dunne
 */
public class AgileNavigateItemProvider implements XNavigateItemProvider {

   public static final XNavItemCat AGILE = new XNavItemCat("Agile");
   public static final XNavItemCat AGILE_REPORTS = new XNavItemCat("Agile.Reports");
   public static final XNavItemCat AGILE_CONFIG = new XNavItemCat("Agile.Configuration");
   public static final XNavItemCat AGILE_CONVERSIONS = new XNavItemCat("Agile.Conversions");

   @Override
   public boolean isApplicable() {
      return AgileUtil.isAgileUser(AtsApiService.get());
   }

   @Override
   public List<XNavigateItem> getNavigateItems(List<XNavigateItem> items) {
      ElapsedTime time = new ElapsedTime("NVI - agile section", AtsNavigateViewItems.debug);
      try {

         items.add(new XNavigateItemFolder(AGILE.getName(),
            AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_TEAM), XNavItemCat.MID));

         items.add(new XNavigateItemFolder("Reports", AtsImage.REPORT, AGILE_REPORTS, SUBCAT));
         items.add(new XNavigateItemFolder("Configuration", FrameworkImage.GEAR, AGILE_CONFIG, SUBCAT));
         items.add(new XNavigateItemFolder("Conversions", FrameworkImage.VERSION, AGILE_CONVERSIONS, SUBCAT));

         items.add(new OpenAgileBacklog());
         items.add(new OpenAgileSprint());
         items.add(new SortAgileBacklog());

         // Reports
         items.add(new XNavigateItemAction(new SprintReportAction(null, true), FrameworkImage.REPORT, AGILE_REPORTS));
         items.add(new OpenAgileSprintReports());
         items.add(new OpenAgileStoredSprintReports());
         items.add(new XNavigateItemBlam(new SyncJiraAndOseeByEpicBlam(), AGILE_REPORTS));
         items.add(new XNavigateItemBlam(new SyncJiraAndOseeBlam(), AGILE_REPORTS));

         // Configs
         items.add(new CreateNewAgileTeam());
         items.add(new CreateNewAgileFeatureGroup());
         items.add(new CreateNewAgileSprint());
         items.add(new CreateNewAgileBacklog());

         // Conversions
         items.add(new ConvertVersionToAgileSprint());

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create Agile section");
      }
      time.end();
      return items;
   }

}
