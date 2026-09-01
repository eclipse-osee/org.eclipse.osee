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
	input,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { UiService } from '@osee/shared/services';
import { DispatchTabComponent } from './dispatch-tab.component';
import { DispatchConfigService } from './dispatch-config.service';
import { normalizeDispatchConfig } from './dispatch-config.normalizer';
import { slugify } from './dispatch.utils';
import type { BranchType } from './dispatch.types';

@Component({
	selector: 'osee-dispatch',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		MatTab,
		MatTabGroup,
		MatIcon,
		MatIconButton,
		MatMenu,
		MatMenuItem,
		MatMenuTrigger,
		MatTooltip,
		RouterLink,
		DispatchTabComponent,
	],
	template: `
		<div class="tw-p-4">
			@if (loading()) {
				<p class="tw-mb-4 tw-text-lg">Loading Configuration...</p>
			} @else if (config(); as config) {
				@if (config.title) {
					<div
						class="tw-mb-4 tw-flex tw-items-center tw-gap-2 tw-border-b tw-border-solid tw-border-foreground-divider tw-pb-3 tw-shadow-sm">
						<h1 class="tw-m-0 tw-text-2xl tw-font-bold">
							{{ config.title }}
						</h1>
						<div class="tw-flex tw-items-center tw-gap-1">
							<a
								routerLink="/ple/dispatch"
								queryParamsHandling="preserve"
								matTooltip="Dispatch Home"
								aria-label="Dispatch Home">
								<button mat-icon-button>
									<mat-icon>home</mat-icon>
								</button>
							</a>
							<button
								mat-icon-button
								[matMenuTriggerFor]="pagesMenu"
								matTooltip="Navigate to Page"
								aria-label="Navigate to Page">
								<mat-icon>menu</mat-icon>
							</button>
							<mat-menu #pagesMenu="matMenu">
								@for (
									page of allConfigs();
									track page.artifactId
								) {
									<a
										mat-menu-item
										queryParamsHandling="preserve"
										[routerLink]="
											'/ple/dispatch/' +
											slugify(page.name)
										">
										{{ page.config.title || page.name }}
									</a>
								}
							</mat-menu>
						</div>
					</div>
				}

				<mat-tab-group
					class="[--mat-tab-header-label-text-size:1.125rem] [--mat-tab-header-label-text-weight:700]"
					[selectedIndex]="selectedTabIndex()"
					(selectedIndexChange)="onTabChange($event)">
					@for (tab of config.tabs; track tab.key) {
						<mat-tab [label]="tab.label">
							<div class="tw-p-4">
								<osee-dispatch-tab
									[tab]="tab"
									[branchId]="currentBranchId()"
									[branchType]="branchType()" />
							</div>
						</mat-tab>
					}
				</mat-tab-group>
			} @else {
				<div class="tw-p-4">
					<p class="tw-text-sm tw-opacity-50">
						Dispatch page not found.
					</p>
				</div>
			}
		</div>
	`,
})
export class DispatchComponent {
	pageKey = input<string>('');
	tab = input<string>('');
	branchType = input<BranchType>('');

	private readonly uiService = inject(UiService);
	private readonly router = inject(Router);
	private readonly configService = inject(DispatchConfigService);

	private readonly routeBranchId = toSignal(this.uiService.id, {
		initialValue: '',
	});

	readonly currentBranchId = computed(() => {
		const id = this.routeBranchId() || '';
		return String(id).trim();
	});

	// undefined until the first emission so the template can distinguish
	// "still loading" from "loaded, but no pages".
	private readonly configsResult = toSignal(this.configService.getConfigs(), {
		initialValue: undefined,
	});

	protected readonly slugify = slugify;

	protected readonly loading = computed(
		() => this.configsResult() === undefined
	);

	/** Resolved configs, or an empty list while still loading. */
	protected readonly allConfigs = computed(() => this.configsResult() ?? []);

	readonly config = computed(() => {
		const pages = this.allConfigs();
		const key = this.pageKey();
		if (!pages.length) return null;

		// Find the page matching the route key
		const match = pages.find((p) => slugify(p.name) === key);
		if (!match) return null;

		return normalizeDispatchConfig(match.config);
	});

	readonly selectedTabIndex = computed(() => {
		const config = this.config();
		const tabKey = this.tab();
		if (!config || !tabKey) return 0;
		const index = config.tabs.findIndex((t) => t.key === tabKey);
		return index >= 0 ? index : 0;
	});

	onTabChange(index: number): void {
		const config = this.config();
		if (!config) return;
		const tab = config.tabs[index];
		if (!tab) return;
		this.router.navigate(['/ple/dispatch', this.pageKey(), tab.key], {
			queryParamsHandling: 'merge',
			replaceUrl: true,
		});
	}

	private readonly syncBranchEffect = effect(() => {
		const branchType = this.branchType();
		if (branchType) this.uiService.typeValue = branchType;
	});

	/** If no tab is in the URL, redirect to include the first tab's key. */
	private readonly syncInitialTab = effect(() => {
		const config = this.config();
		const currentTab = this.tab();
		const pageKey = this.pageKey();
		if (config && !currentTab && pageKey && config.tabs.length > 0) {
			this.router.navigate(
				['/ple/dispatch', pageKey, config.tabs[0].key],
				{ replaceUrl: true, queryParamsHandling: 'merge' }
			);
		}
	});
}

export default DispatchComponent;
