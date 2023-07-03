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
import { MessageTypesService } from '@osee/messaging/shared/services';
import { messageTypesMock } from '@osee/messaging/shared/testing';
import { of } from 'rxjs';

export const messageTypesServiceMock: Partial<MessageTypesService> = {
	getFiltered: () => of(messageTypesMock),
	getOne: () => of(messageTypesMock[0]),
	getCount: () => of(messageTypesMock.length),
};
