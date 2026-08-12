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
import { NavigationEnd, Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { filter } from 'rxjs';
import { UiService, ViewsRoutedUiService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class BranchRoutedUIService {
	private branchService = inject(UiService);
	private router = inject(Router);
	private viewRouteState = inject(ViewsRoutedUiService);

	constructor() {
		// Sync state from query params on initial load
		this.syncFromUrl(this.router.url);

		// Re-sync whenever navigation completes (e.g., sidebar navigation)
		this.router.events
			.pipe(
				filter((e): e is NavigationEnd => e instanceof NavigationEnd),
				takeUntilDestroyed()
			)
			.subscribe((e) => this.syncFromUrl(e.urlAfterRedirects));
	}

	/**
	 * Reads branchType and branchId from the URL query params and updates
	 * the service state if the URL values differ from current state.
	 * This is intentionally one-way (URL → state) and only fires on navigation events,
	 * not on programmatic state changes (which go state → URL via the setters).
	 */
	private syncFromUrl(url: string): void {
		const params = this.router.parseUrl(url).queryParams;
		const urlType =
			(params['branchType'] as 'working' | 'baseline' | '') || '';
		const urlId = params['branchId'] || '';

		if (urlType !== this.branchService.type.getValue()) {
			this.branchService.typeValue = urlType;
		}
		if (urlId !== this.branchService.id.getValue()) {
			this.branchService.idValue = urlId;
		}
	}

	set branchType(value: 'working' | 'baseline' | '') {
		this.branchService.typeValue = value;
		this.branchService.idValue = '';
		const tree = this.router.parseUrl(this.router.url);
		const params = { ...tree.queryParams };
		if (value) {
			params['branchType'] = value;
		} else {
			delete params['branchType'];
		}
		delete params['branchId'];
		delete params['view'];
		tree.queryParams = params;
		this.router.navigateByUrl(tree);
	}

	get type() {
		return this.branchService.type;
	}

	get id() {
		return this.branchService.id;
	}

	set branchId(value: string) {
		this.branchService.idValue = value;
		const tree = this.router.parseUrl(this.router.url);
		const params = { ...tree.queryParams };
		if (value) {
			params['branchId'] = value;
		} else {
			delete params['branchId'];
		}
		delete params['view'];
		tree.queryParams = params;
		this.router.navigateByUrl(tree);
	}

	/**
	 * Sets both branchType and branchId at once.
	 */
	set position(value: { type: 'working' | 'baseline' | ''; id: string }) {
		this.branchService.typeValue = value.type;
		this.branchService.idValue = value.id;
		const tree = this.router.parseUrl(this.router.url);
		const params = { ...tree.queryParams };
		if (value.type) {
			params['branchType'] = value.type;
		} else {
			delete params['branchType'];
		}
		if (value.id) {
			params['branchId'] = value.id;
		} else {
			delete params['branchId'];
		}
		tree.queryParams = params;
		this.router.navigateByUrl(tree);
	}
}
