/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
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
   private final String staticId;

   public WfeEditorAddSupportingFiles(IAtsWorkItem workItem, Collection<File> supportingFiles, String staticId) {
      super("Add Supporting Files");
      this.workItem = workItem;
      this.supportingFiles = supportingFiles;
      this.staticId = staticId;
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
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(), "Import and relate supporting files");
      for (File file : supportingFiles) {
         IArtifactExtractor extractor =
            ArtifactExplorerDragAndDrop.getArtifactExtractor(CoreArtifactTypes.GeneralDocument);
         RoughArtifactCollector collector = new RoughArtifactCollector(new RoughArtifact());
         IArtifactImportResolver resolver =
            ArtifactResolverFactory.createResolver(ArtifactCreationStrategy.CREATE_ON_NEW_ART_GUID,
               CoreArtifactTypes.GeneralDocument, Arrays.asList(CoreAttributeTypes.Name), true, false);

         SourceToRoughArtifactOperation sourceToRoughArtifactOperation =
            new SourceToRoughArtifactOperation(null, extractor, file, collector);
         sourceToRoughArtifactOperation.run(null);
         Artifact workItemArt = AtsApiService.get().getQueryServiceIde().getArtifact(workItem);
         RoughToRealArtifactOperation roughToRealArtifactOperation =
            new RoughToRealArtifactOperation(transaction, workItemArt, collector, resolver, false, extractor);
         roughToRealArtifactOperation.setAddRelation(false);
         roughToRealArtifactOperation.run(null);
         Artifact supportingArt = roughToRealArtifactOperation.getCreatedArtifacts().iterator().next();
         transaction.addArtifact(supportingArt);
         workItemArt.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, supportingArt);
         if (Strings.isValid(staticId)) {
            supportingArt.addAttribute(CoreAttributeTypes.StaticId, staticId);
         }
         transaction.addArtifact(workItemArt);
      }
      transaction.execute();
      return Status.OK_STATUS;
   }

}
