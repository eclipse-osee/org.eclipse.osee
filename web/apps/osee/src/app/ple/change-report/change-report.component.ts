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
import { Component, input, effect, inject } from '@angular/core';
import { ChangeReportTableComponent } from '../artifact-explorer/lib/components/change-report-table/change-report-table.component';
import { UiService } from '@osee/shared/services';
import { ActraPageTitleComponent } from '../../actra/actra-page-title/actra-page-title.component';

@Component({
	selector: 'osee-change-report',
	imports: [ChangeReportTableComponent, ActraPageTitleComponent],
	template: `
		<osee-actra-page-title
			title="Change Report"
			icon="differences" />
		@if (branchId() !== undefined && branchId() !== '') {
			<osee-change-report-table
				[branchId]="branchId()"></osee-change-report-table>
		}
	`,
})
export class ChangeReportComponent {
	branchId = input<string>('');

	private uiService = inject(UiService);

	constructor() {
		effect(() => {
			const id = this.branchId();
			this.uiService.idValue = id ?? '';

			if (id === undefined || id === '') {
				this.uiService.setErrorTextAndDetails(
					'Set Branch ID in the URL',
					'For example: `{domain}/ple/change-report/{branch-id}`'
				);
			}
		});
	}
}
export default ChangeReportComponent;
