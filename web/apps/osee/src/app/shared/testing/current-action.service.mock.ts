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
import { CurrentActionService } from '@osee/shared/services';
import { of } from 'rxjs';
import { teamWorkflowDetailsMock } from '../testing/configuration-management.response.mock';

export const currentActionServiceMock: Partial<CurrentActionService> = {
	branchWorkFlow: of(teamWorkflowDetailsMock),
};
