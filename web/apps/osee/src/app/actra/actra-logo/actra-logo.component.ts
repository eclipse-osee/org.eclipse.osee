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
import { Component } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
	selector: 'osee-actra-logo',
	imports: [MatIcon, RouterLink],
	template: `
		<a [routerLink]="'/actra/world'">
			<div class="tw-flex tw-gap-3">
				<mat-icon
					class="material-icons-outlined tw-align-center tw-flex tw-scale-150 tw-justify-center tw-text-lg tw-text-black dark:tw-text-white"
					>pending_actions</mat-icon
				>
				<span class="tw-italics tw-font-bold">AcTra</span>
			</div>
		</a>
	`,
})
export default class ActraLogoComponent {}
