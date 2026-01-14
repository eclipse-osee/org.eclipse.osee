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
import { Component } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
	selector: 'osee-osee-logo',
	imports: [MatIcon, RouterLink],
	template: `<a
		class="tw-flex tw-gap-3"
		routerLink="/">
		<mat-icon
			class="tw-size-[55px] tw-text-black dark:tw-text-white"
			svgIcon="osee_logo"></mat-icon>
	</a>`,
})
export default class OseeLogoComponent {}
