/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import type { branchApplicability } from '@osee/messaging/shared/types';
import { of } from 'rxjs';
import { EditAuthService } from '../services/public/edit-auth-service.service';

export const editAuthServiceMock: Partial<EditAuthService> = {
	get branchEditability() {
		return of<branchApplicability>({
			associatedArtifactId: '-1',
			branch: {
				id: '-1',
				viewId: '-1',
				idIntValue: -1,
				name: '',
			},
			editable: false,
			features: [],
			groups: [],
			parentBranch: {
				id: '-1',
				viewId: '-1',
				idIntValue: -1,
				name: '',
			},
			views: [],
		});
	},
};
