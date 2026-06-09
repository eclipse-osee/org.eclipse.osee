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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { SiteBannerService } from './site-banner.service';

@Component({
	selector: 'osee-site-banner',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [MatIconButton, MatIcon],
	template: `
		@if (showBanner()) {
			<div
				class="tw-relative tw-w-full tw-bg-primary-800 tw-px-6 tw-py-4 tw-text-sm tw-text-primary-800-contrast tw-shadow-md dark:tw-bg-primary-900 dark:tw-text-primary-900-contrast"
				role="alert">
				<div
					class="tw-mx-auto tw-max-w-4xl tw-text-center tw-leading-relaxed"
					[innerHTML]="bannerContent()"></div>
				<button
					mat-icon-button
					class="tw-absolute tw-right-2 tw-top-2 tw-text-primary-300 hover:tw-text-white"
					(click)="dismiss()"
					aria-label="Dismiss banner">
					<mat-icon>close</mat-icon>
				</button>
			</div>
		}
	`,
})
export class SiteBannerComponent {
	private readonly bannerService = inject(SiteBannerService);
	private readonly config = toSignal(this.bannerService.bannerConfig$, {
		initialValue: { content: '' },
	});
	private readonly dismissed = signal(false);

	protected readonly bannerContent = computed(() => this.config().content);

	protected readonly showBanner = computed(() => {
		const content = this.config().content;
		if (content.length === 0) {
			return false;
		}
		if (!this.dismissed()) {
			return !this.bannerService.isDismissedForContent(content);
		}
		return false;
	});

	/** Dismisses the banner for today and this specific content. */
	protected dismiss() {
		this.bannerService.dismiss(this.config().content);
		this.dismissed.set(true);
	}
}
