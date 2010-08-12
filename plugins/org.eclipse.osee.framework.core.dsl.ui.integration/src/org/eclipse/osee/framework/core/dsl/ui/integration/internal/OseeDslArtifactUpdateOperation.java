/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.dsl.integration.util.OseeDslSegmentParser;
import org.eclipse.osee.framework.core.dsl.integration.util.OseeDslSegmentParser.OseeDslSegment;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Roberto E. Escobar
 */
public class OseeDslArtifactUpdateOperation extends AbstractOperation {

   private final File file;
   private final OseeDslSegmentParser parser;

   public OseeDslArtifactUpdateOperation(OseeDslSegmentParser parser, File file) {
      super("OseeDsl Artifact Update", Activator.PLUGIN_ID);
      this.file = file;
      this.parser = parser;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      String source = getSource();
      Collection<OseeDslSegment> segments = parser.getSegments(source);
      if (segments.isEmpty()) {
         throw new OseeStateException("No tagged segments Found");
      } else {
         double workPercentage = 0.80 * (1.0 / segments.size());
         int workAmount = calculateWork(workPercentage);

         Map<Branch, SkynetTransaction> transactionMap = new HashMap<Branch, SkynetTransaction>();
         for (OseeDslSegment segment : segments) {
            int startAt = segment.end();
            int endAt = segment.start();

            String data = source.substring(startAt, endAt);
            addChanges(transactionMap, segment.getBranchGuid(), segment.getArtifactGuid(), data);
            monitor.worked(workAmount);
         }
         monitor.setTaskName("Persist...");
         for (SkynetTransaction transaction : transactionMap.values()) {
            transaction.execute();
         }
         monitor.worked(calculateWork(0.20));
      }
   }

   protected String getSource() throws IOException {
      return Lib.fileToString(file);
   }

   protected void addChanges(Map<Branch, SkynetTransaction> transactionMap, String branchGuid, String artifactGuid, String data) throws OseeCoreException {
      Branch branch = BranchManager.getBranchByGuid(branchGuid);
      SkynetTransaction transaction = transactionMap.get(branch);
      if (transaction == null) {
         transaction = new SkynetTransaction(branch, "OseeDslArtifactUpdate");
         transactionMap.put(branch, transaction);
      }
      Artifact artifact = ArtifactQuery.getArtifactFromId(artifactGuid, branch);
      artifact.setSoleAttributeFromString(CoreAttributeTypes.GeneralStringData, data);
      artifact.persist(transaction);
   }

}
