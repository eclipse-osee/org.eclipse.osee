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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { map } from 'rxjs';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
	selector: 'osee-change-report-button',
	imports: [MatButton, MatIcon],
	template: `@if (branchId() && branchType()) {
		<button
			mat-flat-button
			class="tw-flex tw-justify-center tw-bg-osee-blue-7 tw-text-background-background dark:tw-bg-osee-blue-10 [&_*]:tw-m-0"
			(click)="openChangeReport()"
			matTooltip="Change Report">
			<mat-icon class="material-icons-outlined">differences</mat-icon>
		</button>
	}`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeReportButtonComponent {
	private currentBranchService = inject(CurrentBranchInfoService);
	private tabService = inject(ArtifactExplorerTabService);
	private uiService = inject(UiService);

	protected branchType = toSignal(this.uiService.type, { initialValue: '' });
	protected branchId = toSignal(this.uiService.id, { initialValue: '' });

	branchName = toSignal(
		this.currentBranchService.currentBranch.pipe(
			map((branch) => branch.name)
		)
	);

	openChangeReport() {
		this.tabService.addChangeReportTab(
			'Change Report - ' + this.branchName()
		);
	}
}
