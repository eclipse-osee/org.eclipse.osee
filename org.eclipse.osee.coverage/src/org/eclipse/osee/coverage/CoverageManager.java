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
package org.eclipse.osee.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.coverage.blam.AbstractCoverageBlam;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamContributionManager;

/**
 * @author Donald G. Dunne
 */
public class CoverageManager implements IFrameworkTransactionEventListener, IArtifactsPurgedEventListener {

   private static Set<ICoverageEditorItem> cache = new HashSet<ICoverageEditorItem>();

   public static void importCoverage(ICoverageImporter coverageImporter) throws OseeCoreException {
      CoverageImport coverageImport = coverageImporter.run();
      CoverageEditor.open(new CoverageEditorInput(coverageImport));
   }

   public static Collection<AbstractCoverageBlam> getCoverageBlams() {
      List<AbstractCoverageBlam> blams = new ArrayList<AbstractCoverageBlam>();
      for (AbstractBlam blam : BlamContributionManager.getBlamOperations()) {
         if (blam instanceof AbstractCoverageBlam) {
            blams.add((AbstractCoverageBlam) blam);
         }
      }
      return blams;
   }

   public static Collection<CoveragePackage> getCoveragePackages() throws OseeCoreException {
      List<CoveragePackage> packages = new ArrayList<CoveragePackage>();
      for (Artifact artifact : ArtifactQuery.getArtifactListFromType(CoveragePackage.ARTIFACT_NAME,
            BranchManager.getCommonBranch())) {
         ICoverageEditorItem item = getByGuid(artifact.getGuid());
         if (item == null) {
            packages.add(new CoveragePackage(artifact));
         } else {
            packages.add((CoveragePackage) item);
         }
      }
      return packages;
   }

   public static ICoverageEditorItem getByGuid(String guid) {
      for (ICoverageEditorItem item : cache) {
         if (item.getGuid().equals(guid)) {
            return item;
         }
      }
      return null;
   }

   public static void cache(ICoverageEditorItem item) {
      cache.add(item);
   }

   public static void deCache(ICoverageEditorItem item) {
      cache.remove(item);
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      for (Artifact artifact : transData.cacheDeletedArtifacts) {
         if (artifact instanceof ICoverageEditorItem) {
            deCache((ICoverageEditorItem) artifact);
         }
      }
   }

   @Override
   public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      for (Artifact artifact : loadedArtifacts.getLoadedArtifacts()) {
         if (artifact instanceof ICoverageEditorItem) {
            deCache((ICoverageEditorItem) artifact);
         }
      }
   }
}
