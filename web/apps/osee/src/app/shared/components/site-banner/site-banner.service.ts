/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { apiURL } from '@osee/environments';
import { Observable, of } from 'rxjs';
import { catchError, shareReplay } from 'rxjs/operators';
import { SiteBannerConfig } from './site-banner.types';

const DISMISSED_KEY = 'osee-site-banner-dismissed-date';

@Injectable({
	providedIn: 'root',
})
export class SiteBannerService {
	private readonly http = inject(HttpClient);

	readonly bannerConfig$: Observable<SiteBannerConfig> = this.http
		.get<SiteBannerConfig>(apiURL + '/orcs/datastore/banner')
		.pipe(
			catchError(() => of({ content: '' })),
			shareReplay({ bufferSize: 1, refCount: true })
		);

	isDismissed(): boolean {
		const dismissedDate = localStorage.getItem(DISMISSED_KEY);
		if (!dismissedDate) {
			return false;
		}
		const today = new Date().toDateString();
		return dismissedDate === today;
	}

	dismiss(): void {
		localStorage.setItem(DISMISSED_KEY, new Date().toDateString());
	}
}
