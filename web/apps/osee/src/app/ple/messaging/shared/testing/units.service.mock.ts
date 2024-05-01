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
import { UnitsService } from '@osee/messaging/shared/services';
import { unitsMock } from '@osee/messaging/shared/testing';
import { of } from 'rxjs';

export const unitsServiceMock: Partial<UnitsService> = {
	getFiltered: () => of(unitsMock),
	getOne: () => of(unitsMock[0]),
	getCount: () => of(unitsMock.length),
};
