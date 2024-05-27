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
import { NamedId } from '@osee/shared/types';
import { transactionResult } from '@osee/transactions/types';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { CurrentMessagePeriodicityService } from '@osee/messaging/message-periodicity/services';

export const CurrentMessagePeriodicitiesServiceMock: Partial<CurrentMessagePeriodicityService> =
	{
		getMessagePeriodicity: function (id: string): Observable<NamedId> {
			return of();
		},
		getFilteredPaginatedMessagePeriodicities: function (
			pageNum: string | number,
			filter?: string | undefined
		): Observable<NamedId[]> {
			return of([]);
		},
		getFilteredCount: function (
			filter?: string | undefined
		): Observable<number> {
			return of(10);
		},
		currentPageSize: of(10),
		currentPage: new BehaviorSubject(1),
		page: 0,
		pageSize: 0,
		set filter(value: string) {},
		modifyMessagePeriodicity: function (
			value: NamedId
		): Observable<transactionResult> {
			return of();
		},
		createMessagePeriodicity: function (
			value: string
		): Observable<transactionResult> {
			return of();
		},
	};
