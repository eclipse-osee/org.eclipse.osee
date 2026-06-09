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

const DISMISSED_KEY = 'osee-site-banner-dismissed';

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

	/** Returns true if the banner was dismissed today for this exact content. */
	isDismissedForContent(content: string): boolean {
		const stored = localStorage.getItem(DISMISSED_KEY);
		if (!stored) {
			return false;
		}
		try {
			const parsed = JSON.parse(stored) as {
				date: string;
				content: string;
			};
			const today = new Date().toDateString();
			return parsed.date === today && parsed.content === content;
		} catch {
			return false;
		}
	}

	dismiss(content: string): void {
		const entry = {
			date: new Date().toDateString(),
			content,
		};
		localStorage.setItem(DISMISSED_KEY, JSON.stringify(entry));
	}
}
