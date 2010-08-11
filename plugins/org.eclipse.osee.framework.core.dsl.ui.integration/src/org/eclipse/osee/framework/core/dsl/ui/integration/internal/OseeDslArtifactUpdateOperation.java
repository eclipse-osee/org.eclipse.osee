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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.dsl.integration.util.OseeUtil;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Roberto E. Escobar
 */
public class OseeDslArtifactUpdateOperation extends AbstractOperation {

   private final File file;

   public OseeDslArtifactUpdateOperation(File file) {
      super("OseeDsl Artifact Update", Activator.PLUGIN_ID);
      this.file = file;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      String source = Lib.fileToString(file);
      Pair<String, String> metaData = OseeUtil.fromOseeDslArtifactSource(source);
      Conditions.checkNotNull(metaData, "artifact source", "Unable to find artifact source marker for file [%s]",
         file.getAbsolutePath());

      String branchGuid = metaData.getFirst();
      String artifactGuid = metaData.getSecond();
      Branch branch = BranchManager.getBranchByGuid(branchGuid);
      Artifact artifact = ArtifactQuery.getArtifactFromId(artifactGuid, branch);

      artifact.setSoleAttributeFromString(CoreAttributeTypes.GeneralStringData, source);
      artifact.persist();
   }
}
