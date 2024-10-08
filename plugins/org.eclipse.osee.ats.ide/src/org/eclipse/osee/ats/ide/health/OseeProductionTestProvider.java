/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.ide.health;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;

public interface OseeProductionTestProvider {

   public Collection<StandAloneRestData> getStandAloneRestDatas();

   public void testPublishing(XResultData rd);

   public ArtifactId getAtsWorkItemQueryTeamDef();

   public String getTestName();

   public Map<String, QueryBuilderArtifact> getAtsQuickSearchQueries();

}
