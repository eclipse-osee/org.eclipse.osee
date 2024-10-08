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

package org.eclipse.osee.ats.api.task.create;

import java.util.Collection;

/**
 * Interface for bundles providing instantiated Workflow Definitions
 *
 * @author Donald G. Dunne
 */
public interface IAtsTaskSetDefinitionProvider {

   Collection<CreateTasksDefinitionBuilder> getTaskSetDefinitions();
}
