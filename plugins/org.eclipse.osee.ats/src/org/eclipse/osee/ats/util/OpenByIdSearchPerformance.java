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
package org.eclipse.osee.ats.util;

import java.rmi.activation.Activator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.world.search.LegacyPcrIdQuickSearch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.AbstractArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;
import org.eclipse.osee.framework.skynet.core.utility.ElapsedTime;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class OpenByIdSearchPerformance extends XNavigateItemAction {

   public OpenByIdSearchPerformance(XNavigateItem parent) {
      super(parent, "OpenByIdSearchPerformance", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      List<String> pcrIds = new ArrayList<String>();
      pcrIds.add("8125");

      try {
         ElapsedTime time = new ElapsedTime("LegacyPCRActions");
         List<Artifact> resultAtsArts = new ArrayList<Artifact>();
         resultAtsArts.addAll(LegacyPCRActions.getTeamsTeamWorkflowArtifacts(pcrIds,
            (Collection<IAtsTeamDefinition>) null));
         System.out.println("Found " + resultAtsArts.size());
         time.end();

         // decache to make it fair
         for (Artifact resulArtifact : resultAtsArts) {
            ArtifactCache.deCache(resulArtifact);
         }
         resultAtsArts.clear();

         time = new ElapsedTime("getArtifactListFromAttribute");
         resultAtsArts.addAll(ArtifactQuery.getArtifactListFromAttribute(AtsAttributeTypes.LegacyPcrId, "8125",
            AtsUtil.getAtsBranchToken()));
         System.out.println("Found " + resultAtsArts.size());
         time.end();

         // decache to make it fair
         for (Artifact resulArtifact : resultAtsArts) {
            ArtifactCache.deCache(resulArtifact);
         }
         resultAtsArts.clear();

         time = new ElapsedTime("criteria search");
         List<AbstractArtifactSearchCriteria> criteria = new ArrayList<AbstractArtifactSearchCriteria>(4);
         criteria.add(new AttributeCriteria(AtsAttributeTypes.LegacyPcrId, pcrIds));
         ArtifactQuery.getArtifactListFromCriteria(AtsUtil.getAtsBranch(), 200, criteria);
         System.out.println("Found " + resultAtsArts.size());
         time.end();

         // decache to make it fair
         for (Artifact resulArtifact : resultAtsArts) {
            ArtifactCache.deCache(resulArtifact);
         }
         resultAtsArts.clear();

         time = new ElapsedTime("attr quicksearch");
         LegacyPcrIdQuickSearch search = new LegacyPcrIdQuickSearch(Arrays.asList("8125"));
         resultAtsArts.addAll(search.performSearch());
         System.out.println("Found " + resultAtsArts.size());
         time.end();

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }
}
