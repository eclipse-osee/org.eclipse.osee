/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest;

import java.io.File;
import org.eclipse.define.api.importing.IArtifactExtractor;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.define.api.importing.RoughArtifactKind;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.api.ImportOperations;
import org.eclipse.osee.define.rest.importing.operations.RoughToRealArtifactOperation;
import org.eclipse.osee.define.rest.importing.operations.SourceToRoughArtifactOperation;
import org.eclipse.osee.define.rest.importing.parsers.WordOutlineExtractor;
import org.eclipse.osee.define.rest.importing.parsers.WordOutlineExtractorDelegate;
import org.eclipse.osee.define.rest.importing.resolvers.ArtifactResolverFactory;
import org.eclipse.osee.define.rest.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.define.rest.importing.resolvers.ArtifactResolverFactory.ArtifactCreationStrategy;
import org.eclipse.osee.define.rest.operations.ArtifactValidationCheckOperation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author David W. Miller
 */
public class ImportOperationsImpl implements ImportOperations {
   private final OrcsApi orcsApi;
   private final ActivityLog activityLog;

   public ImportOperationsImpl(OrcsApi orcsApi, ActivityLog activityLog) {
      this.orcsApi = orcsApi;
      this.activityLog = activityLog;
   }

   @Override
   public TransactionToken importWord(BranchId branch, String wordURI, ArtifactId parent) {
      Conditions.checkNotNull(branch, "branch query param");
      Conditions.checkNotNull(wordURI, "selected_types query param");
      Conditions.checkNotNull(parent, "parent Artifact");
      IArtifactExtractor extractor = new WordOutlineExtractor();
      extractor.setDelegate(new WordOutlineExtractorDelegate());
      RoughArtifactCollector collector =
         new RoughArtifactCollector(new RoughArtifact(orcsApi, activityLog, RoughArtifactKind.PRIMARY));
      SourceToRoughArtifactOperation sourceOp =
         new SourceToRoughArtifactOperation(orcsApi, activityLog, extractor, new File(wordURI), collector);
      sourceOp.importFiles();
      TransactionBuilder transaction =
         orcsApi.getTransactionFactory().createTransaction(branch, SystemUser.OseeSystem, "Server word import");
      ArtifactReadable parentArtifact = orcsApi.getQueryFactory().fromBranch(branch).andId(parent).getArtifact();
      IArtifactImportResolver resolver = ArtifactResolverFactory.createResolver(transaction,
         ArtifactCreationStrategy.CREATE_NEW_ALWAYS, CoreArtifactTypes.SubsystemRequirementMSWord, null, true, false);
      RoughToRealArtifactOperation roughToReal = new RoughToRealArtifactOperation(orcsApi, activityLog, transaction,
         parentArtifact, collector, resolver, false, extractor);
      roughToReal.doWork();

      ArtifactValidationCheckOperation validator =
         new ArtifactValidationCheckOperation(orcsApi, activityLog, parentArtifact, true);
      XResultData results = validator.validate();
      return transaction.commit();
   }
}
