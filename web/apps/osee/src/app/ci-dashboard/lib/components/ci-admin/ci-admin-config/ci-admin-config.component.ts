/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { Component, computed, inject } from '@angular/core';
import { PersistedNumberAttributeInputComponent } from '@osee/attributes/persisted-number-attribute-input';
import { CiAdminConfigService } from '../../../services/ci-admin-config.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { ciConfigSentinel } from '../../../types/ci-config';
import { FormsModule } from '@angular/forms';
import { filter, repeat, switchMap, take, tap } from 'rxjs';
import { UiService } from '@osee/shared/services';

@Component({
	selector: 'osee-ci-admin-config',
	imports: [PersistedNumberAttributeInputComponent, FormsModule],
	template: `
		<h5 class="tw-pl-4">Settings</h5>
		@if (ciConfig().id !== '-1') {
			@if (
				ciConfig().branch.id === commonBranch &&
				branchType() === 'working'
			) {
				<p class="tw-px-4">
					Currently using the global Zenith configuration. If
					different settings are needed for this branch,
					<button
						class="tw-text-primary"
						(click)="createBranchConfig()"
						data-testid="create-config-button">
						Click Here
					</button>
					to create a branch configuration.
				</p>
			}
			<form
				class="tw-max-w-80"
				#defaultForm="ngForm">
				<osee-persisted-number-attribute-input
					[artifactId]="ciConfig().id"
					[artifactApplicability]="ciConfig().applicability"
					[value]="ciConfig().testResultsToKeep"
					[disabled]="isDisabled()"
					label="Number of results to keep"
					tooltip="Number of test results that will be kept per test script per CI Set. When the number of results exceeds this limit, the oldest results are deleted."
					data-testid="results-to-keep"></osee-persisted-number-attribute-input>
			</form>
		} @else {
			<p class="tw-p-4">
				The Zenith Configuration artifact has not been created. Please
				create it to access the settings.
			</p>
		}
	`,
})
export class CiAdminConfigComponent {
	ciConfigService = inject(CiAdminConfigService);
	uiService = inject(UiService);

	branchType = toSignal(this.uiService.type);

	commonBranch = '570';

	ciConfig = toSignal(
		this.uiService.id.pipe(
			filter((id) => id !== '-1' && id !== '0' && id !== ''),
			switchMap((branchId) =>
				this.ciConfigService
					.getCiConfig(branchId)
					.pipe(repeat({ delay: () => this.uiService.update }))
			)
		),
		{
			initialValue: ciConfigSentinel,
		}
	);

	isDisabled = computed(
		() =>
			this.ciConfig().branch.id === this.commonBranch ||
			this.ciConfig().branch.id === '-1'
	);

	createBranchConfig() {
		this.uiService.id
			.pipe(
				take(1),
				filter((id) => id !== '-1' && id !== '0' && id !== ''),
				switchMap((branchId) =>
					this.ciConfigService.createCiConfig(branchId).pipe(take(1))
				),
				tap((res) => {
					if (res.results.errors) {
						this.uiService.ErrorText = res.results.results[0];
					} else {
						this.uiService.updated = true;
					}
				})
			)
			.subscribe();
	}
}
