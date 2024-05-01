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
import { UnreferencedService } from '@osee/messaging/shared/services';
import { of } from 'rxjs';

export const unreferencedServiceMock: Partial<UnreferencedService> = {
	getUnreferencedPlatformTypes: (
		branchId: string,
		filter?: string,
		pageNum?: string | number,
		pageSize?: string | number
	) => of([]),
	getUnreferencedElements: (
		branchId: string,
		filter?: string,
		pageNum?: string | number,
		pageSize?: string | number
	) => of([]),
	getUnreferencedStructures: (
		branchId: string,
		filter?: string,
		pageNum?: string | number,
		pageSize?: string | number
	) => of([]),
	getUnreferencedSubmessages: (
		branchId: string,
		filter?: string,
		pageNum?: string | number,
		pageSize?: string | number
	) => of([]),
	getUnreferencedMessages: (
		branchId: string,
		filter?: string,
		pageNum?: string | number,
		pageSize?: string | number
	) => of([]),
	getUnreferencedPlatformTypesCount: (branchId: string, filter?: string) =>
		of(0),
	getUnreferencedElementsCount: (branchId: string, filter?: string) => of(0),
	getUnreferencedStructuresCount: (branchId: string, filter?: string) =>
		of(0),
	getUnreferencedSubmessagesCount: (branchId: string, filter?: string) =>
		of(0),
	getUnreferencedMessagesCount: (branchId: string, filter?: string) => of(0),
};
