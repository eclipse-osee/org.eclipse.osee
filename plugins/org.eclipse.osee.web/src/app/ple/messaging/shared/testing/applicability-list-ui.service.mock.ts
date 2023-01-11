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
import { of } from 'rxjs';
import { ApplicabilityListUIService } from '../services/ui/applicability-list-ui.service';

export const applicabilityListUIServiceMock: Partial<ApplicabilityListUIService> =
	{
		applic: of([
			{ id: '1', name: 'Base' },
			{ id: '2', name: 'Second' },
		]),
	};
