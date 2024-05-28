/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { CurrentActionService } from '@osee/configuration-management/services';
import { teamWorkflowDetailsMock } from '@osee/shared/testing';
import { of } from 'rxjs';

export const currentActionServiceMock: Partial<CurrentActionService> = {
	branchWorkFlow: of(teamWorkflowDetailsMock),
};
