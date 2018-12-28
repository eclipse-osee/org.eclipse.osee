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
package org.eclipse.osee.ats.editor;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughToRealArtifactOperation;
import org.eclipse.osee.framework.skynet.core.importing.operations.SourceToRoughArtifactOperation;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory.ArtifactCreationStrategy;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorerDragAndDrop;

/**
 * Imports supporting files and relates them to work item
 *
 * @author Donald G. Dunne
 */
public class WfeEditorAddSupportingFiles extends Job {

   private final Collection<File> supportingFiles;
   private final IAtsWorkItem workItem;

   public WfeEditorAddSupportingFiles(IAtsWorkItem workItem, Collection<File> supportingFiles) {
      super("Add Supporting Files");
      this.workItem = workItem;
      this.supportingFiles = supportingFiles;
   }

   public XResultData validate() {
      XResultData results = new XResultData();
      if (supportingFiles.isEmpty()) {
         results.error("Must pass in supporting files");
      }
      for (File file : supportingFiles) {
         if (!file.exists()) {
            results.error("File does not exist " + file.getAbsolutePath());
         }
      }
      return results;
   }

   @Override
   public IStatus run(IProgressMonitor monitor) {
      XResultData results = validate();
      if (results.isErrors()) {
         throw new OseeArgumentException(results.toString());
      }
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(),
         "Import and relate supporting files");
      for (File file : supportingFiles) {
         IArtifactExtractor extractor = ArtifactExplorerDragAndDrop.getArtifactExtractor(
            ArtifactTypeManager.getType(CoreArtifactTypes.GeneralDocument));
         RoughArtifactCollector collector = new RoughArtifactCollector(new RoughArtifact(RoughArtifactKind.PRIMARY));
         IArtifactImportResolver resolver =
            ArtifactResolverFactory.createResolver(ArtifactCreationStrategy.CREATE_ON_NEW_ART_GUID,
               CoreArtifactTypes.GeneralDocument, Arrays.asList(CoreAttributeTypes.Name), true, false);

         SourceToRoughArtifactOperation sourceToRoughArtifactOperation =
            new SourceToRoughArtifactOperation(null, extractor, file, collector);
         sourceToRoughArtifactOperation.run(null);
         Artifact workItemArt = AtsClientService.get().getQueryServiceClient().getArtifact(workItem);
         RoughToRealArtifactOperation roughToRealArtifactOperation =
            new RoughToRealArtifactOperation(transaction, workItemArt, collector, resolver, false, extractor);
         roughToRealArtifactOperation.setAddRelation(false);
         roughToRealArtifactOperation.run(null);
         Artifact artifact = roughToRealArtifactOperation.getCreatedArtifacts().iterator().next();
         transaction.addArtifact(artifact);
         workItemArt.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, artifact);
         transaction.addArtifact(workItemArt);
      }
      transaction.execute();
      return Status.OK_STATUS;
   }

}
