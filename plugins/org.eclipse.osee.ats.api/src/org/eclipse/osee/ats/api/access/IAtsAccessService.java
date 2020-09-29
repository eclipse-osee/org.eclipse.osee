/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.access;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Donald G. Dunne
 */
public interface IAtsAccessService {

   boolean isApplicable(BranchId branch);

   Collection<AccessContextToken> getContextIds(BranchId branch);

   Collection<AccessContextToken> getContextIds(BranchId branch, boolean useCache);

   Map<String, Long> getContextGuidToIdMap();

   Collection<AccessContextToken> getFromWorkflow(IAtsTeamWorkflow teamWf);

   void setContextIds(IAtsObject atsObject, AccessContextToken... contextIds);

   void clearCaches();

}
