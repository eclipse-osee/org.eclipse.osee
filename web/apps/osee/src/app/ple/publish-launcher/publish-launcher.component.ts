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
import { httpResource } from '@angular/common/http';
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import { BranchPickerComponent } from '@osee/shared/components';
import { UiService } from '@osee/shared/services';
import { PublishLauncherTabComponent } from './publish-launcher-tab.component';
import type {
	BranchType,
	PublishLauncherConfig,
} from './publish-launcher.types';

@Component({
	selector: 'osee-publish-launcher',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		MatTab,
		MatTabGroup,
		BranchPickerComponent,
		PublishLauncherTabComponent,
	],
	template: `
		<div class="tw-p-4">
			<div class="tw-mb-4">
				<osee-branch-picker />
			</div>

			@if (loadingGlobal()) {
				<div
					class="tw-mb-4 tw-rounded tw-border tw-border-blue-300 tw-bg-blue-50 tw-p-3 tw-text-blue-900 dark:tw-border-blue-700 dark:tw-bg-blue-950 dark:tw-text-blue-200">
					Loading Publishing Configuration...
				</div>
			} @else if (config(); as config) {
				@if (config.title) {
					<h1 class="tw-mb-6 tw-text-2xl tw-font-bold">
						{{ config.title }}
					</h1>
				}

				<mat-tab-group>
					@for (tab of config.tabs; track tab.key) {
						<mat-tab [label]="tab.label">
							<div class="tw-p-4">
								<osee-publish-launcher-tab
									[tab]="tab"
									[branchId]="currentBranchId()"
									[branchType]="branchType()" />
							</div>
						</mat-tab>
					}
				</mat-tab-group>
			}
		</div>
	`,
})
export class PublishLauncherComponent {
	branchId = input<string>('');
	branchType = input<BranchType>('');

	private readonly uiService = inject(UiService);

	private readonly routeBranchId = toSignal(this.uiService.id, {
		initialValue: '',
	});

	readonly currentBranchId = computed(() => {
		const id = this.branchId() || this.routeBranchId() || '';
		return String(id).trim();
	});

	readonly hasBranch = computed(() => {
		const id = this.currentBranchId();
		return !!id && id !== '0';
	});

	private readonly configResource = httpResource<PublishLauncherConfig>(() =>
		this.hasBranch()
			? '/orcs/branch/570/artifact/10716029/attribute/type/1152921504606847380'
			: undefined
	);

	readonly config = this.configResource.value;
	protected readonly loadingGlobal = this.configResource.isLoading;

	private readonly syncBranchEffect = effect(() => {
		const branchId = this.branchId();
		const branchType = this.branchType();
		if (branchId) this.uiService.idValue = branchId;
		if (branchType) this.uiService.typeValue = branchType;
	});
}

export default PublishLauncherComponent;
