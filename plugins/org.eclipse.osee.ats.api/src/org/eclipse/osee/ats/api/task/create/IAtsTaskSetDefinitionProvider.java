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
