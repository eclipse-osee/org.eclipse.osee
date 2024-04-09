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
import { NgClass } from '@angular/common';
import { Component, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
	selector: 'osee-expand-icon',
	standalone: true,
	imports: [NgClass, MatIcon],
	template: `<div class="tw-flex tw-items-center">
		<mat-icon
			[ngClass]="
				open()
					? 'tw-rotate-90 tw-transform tw-transition-transform tw-duration-200 tw-ease-in'
					: 'tw-rotate-0 tw-transform tw-transition-transform tw-duration-200 tw-ease-in'
			">
			chevron_right
		</mat-icon>
	</div> `,
})
export class ExpandIconComponent {
	open = input.required<boolean>();
}
