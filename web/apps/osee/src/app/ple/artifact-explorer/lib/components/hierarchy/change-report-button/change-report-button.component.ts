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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	input,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { map } from 'rxjs';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';

@Component({
	selector: 'osee-change-report-button',
	imports: [MatButton, MatIcon, MatTooltip, RouterLink],
	template: `@if (branchId() && branchType()) {
		<a
			[routerLink]="'/ple/change-report/' + branchId()"
			target="_blank">
			<button
				mat-flat-button
				class="tw-flex tw-justify-center tw-bg-osee-blue-7 tw-text-background-background dark:tw-bg-osee-blue-10 [&_*]:tw-m-0"
				matTooltip="Change Report">
				<mat-icon class="material-icons-outlined">differences</mat-icon>
			</button>
		</a>
	}`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeReportButtonComponent {
	inputBranchType = input<string>('');
	inputBranchId = input<string>('');

	private currentBranchService = inject(CurrentBranchInfoService);
	private tabService = inject(ArtifactExplorerTabService);
	private uiService = inject(UiService);

	protected serviceBranchType = toSignal(this.uiService.type, {
		initialValue: '',
	});
	protected serviceBranchId = toSignal(this.uiService.id, {
		initialValue: '',
	});

	protected branchType = computed(() =>
		this.inputBranchType() === ''
			? this.serviceBranchType()
			: this.inputBranchType()
	);

	protected branchId = computed(() =>
		this.inputBranchId() === ''
			? this.serviceBranchId()
			: this.inputBranchId()
	);

	branchName = toSignal(
		this.currentBranchService.currentBranch.pipe(
			map((branch) => branch.name)
		)
	);
}
