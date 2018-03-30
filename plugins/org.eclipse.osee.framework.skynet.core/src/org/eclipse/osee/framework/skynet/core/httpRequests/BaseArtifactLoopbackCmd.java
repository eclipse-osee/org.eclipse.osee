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
package org.eclipse.osee.framework.skynet.core.httpRequests;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import java.net.HttpURLConnection;
import java.util.Map;
import org.eclipse.osee.framework.core.client.server.HttpResponse;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseArtifactLoopbackCmd implements IClientLoopbackCmd {

   @Override
   public void execute(final Map<String, String> parameters, final HttpResponse httpResponse) {
      final String branchUuid = parameters.get("branchUuid");
      final String guid = parameters.get("guid");
      final String id = parameters.get("id");
      final boolean isDeleted = Boolean.valueOf(parameters.get("isDeleted"));
      final DeletionFlag searchDeleted = isDeleted ? INCLUDE_DELETED : EXCLUDE_DELETED;
      final String transactionIdStr = parameters.get("transactionId");

      boolean haveAValidBranchIdentifier = Strings.isValid(branchUuid);
      boolean haveAGuid = Strings.isValid(guid);
      boolean haveAnId = Strings.isValid(id);
      if (!(haveAGuid || haveAnId) || !haveAValidBranchIdentifier) {
         httpResponse.outputStandardError(HttpURLConnection.HTTP_BAD_REQUEST,
            String.format("Unable to process [%s]", parameters));
      } else {
         try {
            final Artifact artifact;
            final BranchId branch;
            if (Strings.isValid(transactionIdStr)) {
               TransactionToken transaction = TransactionManager.getTransaction(Long.valueOf(transactionIdStr));
               branch = transaction.getBranch();
               if (haveAGuid) {
                  artifact = ArtifactQuery.getHistoricalArtifactFromId(guid, transaction, searchDeleted);
               } else {
                  artifact =
                     ArtifactQuery.getHistoricalArtifactFromId(ArtifactId.valueOf(id), transaction, searchDeleted);
               }
            } else {
               branch = BranchId.valueOf(branchUuid);
               if (haveAGuid) {
                  artifact = ArtifactQuery.getArtifactFromId(guid, branch, searchDeleted);
               } else {
                  artifact = ArtifactQuery.getArtifactFromId(ArtifactId.valueOf(id), branch, searchDeleted);
               }
            }
            if (artifact == null) {
               httpResponse.outputStandardError(HttpURLConnection.HTTP_NOT_FOUND,
                  String.format("Artifact can not be found in OSEE on branch [%s]", branch));
            } else {
               process(artifact, parameters, httpResponse);
            }
         } catch (Exception ex) {
            httpResponse.outputStandardError(HttpURLConnection.HTTP_INTERNAL_ERROR,
               String.format("Unable to process [%s]", parameters), ex);
         }
      }
   }

   @Override
   public abstract boolean isApplicable(String cmd);

   @Override
   public abstract void process(final Artifact artifact, final Map<String, String> parameters, final HttpResponse httpResponse);
}
