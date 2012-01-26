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
package org.eclipse.osee.define.traceability.operations;

import java.io.File;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.Collection;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.osee.define.traceability.HierarchyHandler;
import org.eclipse.osee.define.traceability.TestUnitTagger;
import org.eclipse.osee.define.utility.IResourceLocator;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author John Misinco
 */
public class TraceResourceDropOperation extends AbstractOperation {

   private final Collection<URI> resources;
   private final IResourceLocator locator;
   private final IRelationTypeSide relTypeSide;
   private final Artifact requirement;
   private final boolean persistChanges;

   public TraceResourceDropOperation(Collection<URI> resources, IRelationTypeSide relTypeSide, Artifact requirement, IResourceLocator locator, boolean persistChanges) {
      super("Trace Resource Drop Operation", Activator.PLUGIN_ID);
      this.relTypeSide = relTypeSide;
      this.requirement = requirement;
      this.resources = resources;
      this.locator = locator;
      this.persistChanges = persistChanges;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!resources.isEmpty()) {
         IOseeBranch branch = requirement.getBranch();
         SkynetTransaction transaction = null;
         if (persistChanges) {
            transaction = TransactionManager.createTransaction(branch, "TraceResourceDrop");
         }
         for (URI resource : resources) {
            File file = new File(resource);
            if (!file.isDirectory()) {
               processFile(file, transaction);
            }
         }
         if (persistChanges) {
            requirement.persist(transaction);
            transaction.execute();
         }
      }
   }

   private void processFile(File file, SkynetTransaction transaction) throws Exception {
      CharBuffer fileBuffer = Lib.fileToCharBuffer(file);
      IFileStore fileStore = EFS.getStore(file.toURI());
      String name = locator.getIdentifier(fileStore, fileBuffer).getName();
      TestUnitTagger tagger = TestUnitTagger.getInstance();
      String tag = tagger.getSourceTag(file.toURI());
      Artifact testUnitArtifact = null;
      IOseeBranch branch = requirement.getBranch();
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
         testUnitArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestCase, branch, tag, null);
         testUnitArtifact.setName(name);
         HierarchyHandler.addArtifact(transaction, testUnitArtifact);
         if (tagSource) {
            tagger.addSourceTag(file.toURI(), testUnitArtifact.getGuid());
         }
      }
      if (!testUnitArtifact.getName().equals(name)) {
         testUnitArtifact.setName(name);
      }

      requirement.addRelation(relTypeSide, testUnitArtifact);
      if (persistChanges) {
         testUnitArtifact.persist(transaction);
      }
   }

}
