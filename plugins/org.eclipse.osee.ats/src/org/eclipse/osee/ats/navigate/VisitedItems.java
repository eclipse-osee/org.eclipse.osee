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
package org.eclipse.osee.ats.navigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class VisitedItems extends XNavigateItemAction {

   public static List<String> visitedGuids = new ArrayList<String>();

   public static List<Artifact> getReverseVisited() throws OseeCoreException {
      // Search artifacts and hold on to references so don't get garbage collected
      Map<String, Artifact> artifacts = new HashMap<String, Artifact>();
      for (Artifact art : ArtifactQuery.getArtifactListFromIds(visitedGuids, AtsUtil.getAtsBranch())) {
         artifacts.put(art.getGuid(), art);
      }
      List<Artifact> revArts = new ArrayList<Artifact>();
      for (int x = visitedGuids.size(); x <= 0; x--) {
         Artifact art = artifacts.get(visitedGuids.get(x));
         if (art != null) {
            revArts.add(art);
         }
      }
      return revArts;
   }

   public static void addVisited(Artifact art) {
      if (!visitedGuids.contains(art.getGuid())) {
         visitedGuids.add(art.getGuid());
      }
   }

   public static void clearVisited() {
      if (visitedGuids != null) {
         visitedGuids.clear();
      }
   }

   public VisitedItems(XNavigateItem parent) {
      super(parent, "My Recently Visited", AtsImage.GLOBE);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      Collection<Artifact> artifacts = ArtifactQuery.getArtifactListFromIds(visitedGuids, AtsUtil.getAtsBranch());
      WorldEditor.open(new WorldEditorSimpleProvider(getName(), artifacts, null, tableLoadOptions));
   }
}
