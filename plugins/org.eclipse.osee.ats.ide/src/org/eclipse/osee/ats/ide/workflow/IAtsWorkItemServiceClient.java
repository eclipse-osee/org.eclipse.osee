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

package org.eclipse.osee.ats.ide.workflow;

import java.util.Set;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkItemHookIde;

public interface IAtsWorkItemServiceClient extends IAtsWorkItemService {

   public Set<IAtsWorkItemHookIde> getWorkItemHooksIde();

   void addWorkItemHookIde(IAtsWorkItemHookIde hook);

}
