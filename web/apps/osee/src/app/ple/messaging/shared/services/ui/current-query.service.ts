/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { Injectable, inject } from '@angular/core';
import { MimQuery } from '@osee/messaging/shared/query';
import { switchMap, shareReplay } from 'rxjs/operators';
import { UiService } from '@osee/shared/services';
import { QueryService } from '../http/query.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentQueryService {
	private ui = inject(UiService);
	private queryService = inject(QueryService);

	query<T = unknown>(query: MimQuery<T>) {
		return this.ui.id.pipe(
			switchMap((id) =>
				this.queryService
					.query(id, query)
					.pipe(shareReplay({ bufferSize: 1, refCount: true }))
			)
		);
	}
	queryExact<T = unknown>(query: MimQuery<T>) {
		return this.ui.id.pipe(
			switchMap((id) =>
				this.queryService
					.queryExact(id, query)
					.pipe(shareReplay({ bufferSize: 1, refCount: true }))
			)
		);
	}
}
