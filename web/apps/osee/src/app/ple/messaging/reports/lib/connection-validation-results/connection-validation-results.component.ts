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
import { Component, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { ObjectValuesPipe } from '@osee/shared/utils';

@Component({
	selector: 'osee-connection-validation-results',
	imports: [ObjectValuesPipe, MatIcon],
	template: `
		<ul class="tw-pl-4">
			<li class="tw-flex tw-items-center tw-gap-2">
				{{ label() }}
				@if ((results() | objectValues).length === 0) {
					<mat-icon class="tw-text-osee-green-9">check</mat-icon>
				}
				@if ((results() | objectValues).length > 0) {
					<mat-icon class="tw-text-osee-red-9">close</mat-icon>
				}
			</li>
			@for (result of results() | objectValues; track result) {
				<li class="tw-pl-4">
					{{ result }}
				</li>
			}
		</ul>
	`,
})
export class ConnectionValidationResultsComponent {
	label = input.required<string>();
	results = input.required<Record<`${number}`, string>>();
}
