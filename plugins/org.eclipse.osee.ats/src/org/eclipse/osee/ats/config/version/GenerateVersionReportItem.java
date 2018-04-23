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
package org.eclipse.osee.ats.config.version;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.util.widgets.dialog.TeamVersionListDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class GenerateVersionReportItem extends XNavigateItemAction {

   public GenerateVersionReportItem(XNavigateItem parent) {
      super(parent, "Generate Version Report", FrameworkImage.VERSION);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      TeamVersionListDialog ld = new TeamVersionListDialog(Active.Both);
      int result = ld.open();
      if (result == 0) {
         IAtsVersion verArt = ld.getSelectedVersion();
         String title = ld.getSelectedTeamDef().getName() + " Version \"" + verArt.getName() + "\" Report";
         VersionReportJob job = new VersionReportJob(title, verArt);
         job.setUser(true);
         job.setPriority(Job.LONG);
         job.schedule();
      }
   }
}
