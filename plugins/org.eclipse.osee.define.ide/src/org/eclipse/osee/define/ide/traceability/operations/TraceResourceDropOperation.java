/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.traceability.operations;

import java.io.File;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.osee.define.ide.traceability.HierarchyHandler;
import org.eclipse.osee.define.ide.traceability.TestUnitTagger;
import org.eclipse.osee.define.ide.utility.IResourceLocator;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author John R. Misinco
 */
public class TraceResourceDropOperation extends AbstractOperation {

   public static interface RenameConfirmer {

      boolean acceptUpdate(Map<Artifact, String> nameUpdateRequired);
   }

   private final Collection<URI> resources;
   private final IResourceLocator locator;
   private final RelationTypeSide relTypeSide;
   private final Artifact requirement;
   private final boolean persistChanges;
   private final RenameConfirmer confirmer;

   public TraceResourceDropOperation(Collection<URI> resources, RelationTypeSide relTypeSide, Artifact requirement, IResourceLocator locator, boolean persistChanges, RenameConfirmer confirmer) {
      super("Trace Resource Drop Operation", Activator.PLUGIN_ID);
      this.relTypeSide = relTypeSide;
      this.requirement = requirement;
      this.resources = resources;
      this.locator = locator;
      this.persistChanges = persistChanges;
      this.confirmer = confirmer;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!resources.isEmpty()) {
         BranchId branch = requirement.getBranch();
         SkynetTransaction transaction = null;
         if (persistChanges) {
            transaction = TransactionManager.createTransaction(branch, "TraceResourceDrop");
         }
         HierarchyHandler handler = new HierarchyHandler(transaction);

         Map<Artifact, String> nameUpdateRequired = new TreeMap<>();

         for (URI resource : resources) {
            File file = new File(resource);
            if (!file.isDirectory()) {
               processFile(file, handler, transaction, nameUpdateRequired);
            }
         }

         boolean isOk = true;
         if (!nameUpdateRequired.isEmpty()) {
            if (confirmer != null) {
               isOk = confirmer.acceptUpdate(nameUpdateRequired);
            }

            if (isOk) {
               for (Entry<Artifact, String> entry : nameUpdateRequired.entrySet()) {
                  entry.getKey().setName(entry.getValue());
               }
            }
         }

         if (transaction != null && persistChanges && isOk) {
            requirement.persist(transaction);
            transaction.execute();
         }
      }
   }

   private void processFile(File file, HierarchyHandler handler, SkynetTransaction transaction, Map<Artifact, String> nameUpdateRequired) throws Exception {
      CharBuffer fileBuffer = Lib.fileToCharBuffer(file);
      URI fileUri = file.toURI();
      IFileStore fileStore = EFS.getStore(fileUri);
      String name = locator.getIdentifier(fileStore, fileBuffer).getName();
      TestUnitTagger tagger = TestUnitTagger.getInstance();
      String tag = tagger.getSourceTag(fileUri);
      Artifact testUnitArtifact = null;
      BranchId branch = requirement.getBranch();
      boolean tagSource = false;
      if (GUID.isValid(tag)) {
         try {
            testUnitArtifact = ArtifactQuery.getArtifactFromId(tag, branch);
         } catch (ArtifactDoesNotExist ex) {
            //do nothing
         }

      } else {
         tag = GUID.create();
         tagSource = true;
      }

      if (testUnitArtifact == null) {
         testUnitArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestCase, branch, name, tag);
         handler.addArtifact(testUnitArtifact);
         if (tagSource) {
            tagger.addSourceTag(fileUri, testUnitArtifact.getGuid());
            refreshFile(file.getAbsolutePath());
         }
      }

      if (!testUnitArtifact.getName().equals(name)) {
         nameUpdateRequired.put(testUnitArtifact, name);
      }

      requirement.addRelation(relTypeSide, testUnitArtifact);
      if (persistChanges) {
         testUnitArtifact.persist(transaction);
      }
   }

   private void refreshFile(String uri) {
      IResource eclipseResource = ResourcesPlugin.getWorkspace().getRoot().findMember(uri);
      if (eclipseResource != null) {
         try {
            eclipseResource.refreshLocal(0, new NullProgressMonitor());
         } catch (CoreException ex) {
            OseeLog.log(Activator.class, Level.INFO, "Refreshing resource failed.");
         }
      }
   }

}
