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
import { Injectable } from '@angular/core';
import {
	share,
	switchMap,
	repeatWhen,
	shareReplay,
	filter,
	take,
} from 'rxjs/operators';
import { ApplicabilityListService, UiService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class ApplicabilityListUIService {
	private _applics = this.ui.id.pipe(
		share(),
		switchMap((id) =>
			this.applicabilityService.getApplicabilities(id).pipe(
				repeatWhen((_) => this.ui.update),
				share()
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _count = this.ui.id.pipe(
		share(),
		switchMap((id) =>
			this.applicabilityService.getApplicabilityCount(id).pipe(
				repeatWhen((_) => this.ui.update),
				share()
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	constructor(
		private ui: UiService,
		private applicabilityService: ApplicabilityListService
	) {}

	_views = this.ui.id.pipe(
		filter((v) => v !== ''),
		switchMap((id) => this.applicabilityService.getViews(id)),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	/**
	 * @deprecated
	 */
	get applic() {
		return this._applics;
	}

	/**
	 * @deprecated
	 */
	get count() {
		return this._count;
	}

	getApplicabilities(
		pageNum: string | number,
		count: number,
		filter?: string
	) {
		return this.ui.id.pipe(
			share(),
			take(1),
			switchMap((id) =>
				this.applicabilityService.getApplicabilities(
					id,
					true,
					pageNum,
					count,
					filter
				)
			)
		);
	}

	getApplicabilityCount(filter?: string) {
		return this.ui.id.pipe(
			share(),
			switchMap((id) =>
				this.applicabilityService.getApplicabilityCount(id, filter)
			)
		);
	}

	get views() {
		return this._views;
	}
}
