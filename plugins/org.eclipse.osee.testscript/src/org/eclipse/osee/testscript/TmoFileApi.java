/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.testscript;

import java.util.Date;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.testscript.internal.ScriptBatchToken;

public interface TmoFileApi {

   public String getBasePath();

   public String getTmoPath(String fileName);

   public String getTmoPath(ScriptResultToken result);

   public String getBatchPath(ScriptBatchToken batch);

   public String createTmoFileName(String scriptName, Date executionDate, ArtifactId ciSetId);

   public String createBatchFileName(String scriptName, Date executionDate, ArtifactId ciSetId, String batchId);

   public String getTmoFolderPath(ArtifactId ciSetId);

   public String getBatchFolderPath(ArtifactId ciSetId, String batchId);
}
