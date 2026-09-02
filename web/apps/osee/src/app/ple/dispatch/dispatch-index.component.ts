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
	effect,
	inject,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import {
	HelpButtonComponent,
	HelpTopicRegistryService,
} from '@osee/shared/components';
import {
	DispatchConfigService,
	DispatchPageConfig,
} from './dispatch-config.service';
import { slugify } from './dispatch.utils';

/**
 * Index page for Dispatch. Lists all available dispatch pages.
 * If only one page exists, redirects directly to it.
 */
@Component({
	selector: 'osee-dispatch-index',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [RouterLink, MatButton, MatIcon, MatTooltip, HelpButtonComponent],
	template: `
		@if (loading()) {
			<div class="tw-p-4">
				<p class="tw-text-lg">Loading Dispatch Pages...</p>
			</div>
		} @else if (pages().length === 0) {
			<div class="tw-p-4">
				<p class="tw-text-sm tw-opacity-50">
					No dispatch configurations found for this version.
				</p>
			</div>
		} @else {
			<div class="tw-p-4">
				<div class="tw-mb-6 tw-flex tw-items-center tw-gap-1">
					<h1 class="tw-m-0 tw-text-2xl tw-font-bold">Dispatch</h1>
					<osee-help-button topicId="dispatch" />
				</div>
				<div class="tw-flex tw-w-full tw-flex-col tw-gap-2">
					@for (page of pages(); track page.artifactId) {
						<a
							mat-stroked-button
							[routerLink]="slugify(page.name)"
							queryParamsHandling="preserve"
							[matTooltip]="getDescription(page)"
							class="tw-w-full tw-justify-start tw-text-foreground-text">
							<mat-icon>open_in_new</mat-icon>
							{{ page.config.title || page.name }}
						</a>
					}
				</div>
			</div>
		}
	`,
})
export class DispatchIndexComponent {
	private readonly configService = inject(DispatchConfigService);
	private readonly router = inject(Router);
	private readonly helpRegistry = inject(HelpTopicRegistryService);

	private readonly _registerHelp = this.helpRegistry.register({
		id: 'dispatch',
		label: 'Dispatch Configuration',
		markdownPath: 'assets/help/dispatch/overview.md',
	});

	private readonly configs = toSignal(this.configService.getConfigs(), {
		initialValue: undefined,
	});

	protected readonly loading = computed(() => this.configs() === undefined);

	protected readonly pages = computed(() => this.configs() ?? []);

	protected readonly slugify = slugify;

	getDescription(page: DispatchPageConfig): string {
		const tabs = page.config.tabs;
		if (!tabs.length) return '';
		return tabs.map((t) => t.label).join(', ');
	}

	/** If only one page exists, redirect to it automatically. */
	private readonly autoRedirect = effect(() => {
		const pages = this.pages();
		if (pages.length === 1) {
			this.router.navigate(['/ple/dispatch', slugify(pages[0].name)], {
				replaceUrl: true,
				queryParamsHandling: 'preserve',
			});
		}
	});
}

export default DispatchIndexComponent;
