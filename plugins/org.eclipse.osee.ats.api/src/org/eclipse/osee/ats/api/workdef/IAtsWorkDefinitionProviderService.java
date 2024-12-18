/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;

public interface IAtsWorkDefinitionProviderService {

   void addWorkDefinitionProvider(IAtsWorkDefinitionProvider workDefProvider);

   WorkDefinition getWorkDefinition(Long id);

   Collection<WorkDefinition> getAll();

   void addWorkDefinition(WorkDefinition workDef);

   void clearCaches();

}