/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.efficiency.ui;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.reports.efficiency.internal.Activator;
import org.eclipse.osee.ats.reports.efficiency.team.TeamEfficiencyModel;
import org.eclipse.osee.ats.reports.efficiency.team.VersionEfficiency;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * Class to extract data from workflows and fills the TeamEfficiencyModel
 * 
 * @author Praveen Joseph
 */
public class LoadEfficiencyDataOperation extends AbstractOperation {

   private final IAtsTeamDefinition teamDef;

   public LoadEfficiencyDataOperation(IAtsTeamDefinition teamDef) {
      super("Load Efficiency Data", Activator.PLUGIN_ID);
      this.teamDef = teamDef;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkNotNull(teamDef, "teamDefinition");
      List<VersionEfficiency> verEffs = new ArrayList<>();
      for (IAtsVersion version : teamDef.getVersions()) {
         VersionEfficiency eff = new VersionEfficiency(version);
         eff.compute();
         verEffs.add(eff);
      }
      TeamEfficiencyModel.setVersionEfficiency(verEffs);
   }

}
