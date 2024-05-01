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
import { RatesService } from '@osee/messaging/shared/services';
import { ratesMock } from '@osee/messaging/shared/testing';
import { of } from 'rxjs';

export const ratesServiceMock: Partial<RatesService> = {
	getFiltered: () => of(ratesMock),
	getOne: () => of(ratesMock[0]),
	getCount: () => of(ratesMock.length),
};
