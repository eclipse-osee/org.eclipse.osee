/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { Component, inject } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { UserDataAccountService } from '@osee/auth';
import { UserRoles } from '@osee/shared/types/auth';
import { Observable, of } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
	selector: 'osee-ple-main',
	templateUrl: './ple.component.html',
	imports: [MatAnchor, RouterLink, CommonModule],
})
export class PleComponent {
	private userService = inject(UserDataAccountService);
	showMIM$: Observable<boolean> = of(true);
	showPLE$: Observable<boolean> = of(true);
	showAE$: Observable<boolean> = of(true);
	constructor() {
		this.showMIM$ = this.userService.userHasRoles([UserRoles.MIM_USER]);
		this.showPLE$ = this.userService.userHasRoles([UserRoles.PLE_USER]);
		this.showAE$ = this.userService.userHasRoles([UserRoles.AE_USER]);
	}
}
export default PleComponent;
