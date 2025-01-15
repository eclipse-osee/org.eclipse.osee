/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { CiAdminConfigService } from '../services/ci-admin-config.service';
import { CIConfig } from '../types/ci-config';
import { applicabilitySentinel } from '@osee/applicability/types';

export const ciAdminConfigServiceMock: Partial<CiAdminConfigService> = {
	getCiConfig(branchId: string) {
		return of(ciAdminConfigResponseMock);
	},
};

export const ciAdminConfigResponseMock: CIConfig = {
	id: '1',
	gammaId: '1',
	testResultsToKeep: {
		id: '111',
		gammaId: '123',
		typeId: '6846375894770628832',
		value: 10,
	},
	branch: { id: '99', viewId: '333' },
	applicability: applicabilitySentinel,
};
