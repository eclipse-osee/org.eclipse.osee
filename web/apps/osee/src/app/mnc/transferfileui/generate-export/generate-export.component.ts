/*********************************************************************
 * Copyright (c) 2024 Boeing
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
	inject,
	signal,
} from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { TransferFileService } from '../../../mnc/services/transfer-file/transfer-file.service';
import { FormsModule } from '@angular/forms';

@Component({
	selector: 'osee-generate-export',
	standalone: true,
	imports: [FormsModule, AsyncPipe],
	template: `<div class="tw-flex tw-place-content-center">
		<form>
			<label
				for="exportIdList"
				class="tw-mb-2 tw-mr-2 tw-border-0 tw-border-b-2 tw-bg-transparent tw-text-sm tw-text-gray-900 dark:tw-text-white">
				Generate A New Export:
				<span class="tw-cursor-help tw-text-sm">
					Select a <strong>"Source Id"</strong> from the available
					menu options then click <strong>"Generate"</strong></span
				>
			</label>

			<select
				id="exportIdList"
				class="focus:border-blue-500 tw-mr-2 tw-rounded-lg tw-border tw-border-gray-300 tw-bg-gray-50 tw-p-2.5 tw-text-sm tw-text-gray-900 dark:tw-border-gray-600 dark:tw-bg-gray-700 dark:tw-text-white dark:tw-placeholder-gray-400 dark:focus:tw-border-blue-500 dark:focus:tw-ring-blue-500"
				[(ngModel)]="selectedSource"
				name="selectedSource">
				<option
					[defaultSelected]="''"
					value="Select a Source...">
					Select a Source...
				</option>
				@for (exportId of exportIdList | async; track exportId) {
					<option [value]="exportId">
						{{ exportId }}
					</option>
				}
			</select>
			<button
				class="tw-mb-2 tw-me-2 tw-space-x-1 tw-rounded-full tw-bg-blue-700 tw-px-5 tw-py-2.5 tw-text-center tw-text-sm tw-font-medium tw-text-white hover:tw-bg-blue-800 focus:tw-outline-none focus:tw-ring-4 focus:tw-ring-blue-300 dark:tw-bg-blue-600 dark:hover:tw-bg-blue-700 dark:focus:tw-ring-blue-800"
				mat-raised-button
				color="primary"
				(click)="generateExport()">
				Generate
			</button>
		</form>
	</div>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GenerateExportComponent {
	protected fileService = inject(TransferFileService);
	exportIdList = this.fileService.exportData.pipe(
		map((data) => data.ids),
		takeUntilDestroyed()
	);
	selectedSource = signal('');
	generateExport() {
		if (this.selectedSource().length > 0) {
			this.fileService.generateExport(this.selectedSource()).subscribe();
		}
	}
}
