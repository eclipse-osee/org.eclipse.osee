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
import { of } from 'rxjs';
import { EnumsService } from '../services/http/enums.service';
import { unitsMock } from './unit.mock';

export const enumsServiceMock: Partial<EnumsService> = {
	rates: of(['1', '10', '15', '20']),
	types: of(['Network', 'Connection']),
	periodicities: of(['Periodic', 'Aperiodic', 'OnDemand']),
	categories: of([
		'BIT Status',
		'Flight Test',
		'Miscellaneous',
		'N/A',
		'Network',
		'Tactical Status',
		'Taskfile',
		'Trackfile',
		'spare',
	]),
	units: of(unitsMock),
};
