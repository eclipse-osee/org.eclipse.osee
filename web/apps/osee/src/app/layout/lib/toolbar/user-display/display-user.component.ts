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
import {
	animate,
	state,
	style,
	transition,
	trigger,
} from '@angular/animations';
import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';
import { RouterOutlet } from '@angular/router';
import { UserDataAccountService } from '@osee/auth';
import { environment } from '@osee/environments';
import { user } from '@osee/shared/types/auth';
import { Observable } from 'rxjs';
import { OktaSignComponent } from '../okta-sign/okta-sign.component';

@Component({
	selector: 'osee-display-user',
	templateUrl: './display-user.component.html',
	styles: [],
	animations: [
		trigger('expandButton', [
			state('closed', style({ transform: 'rotate(0)' })),
			state('open', style({ transform: 'rotate(-180deg)' })),
			transition(
				'open => closed',
				animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)')
			),
			transition(
				'closed => open',
				animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)')
			),
		]),
	],
	standalone: true,
	imports: [
		RouterOutlet,
		AsyncPipe,
		OktaSignComponent,
		MatMenuTrigger,
		MatIcon,
		MatMenu,
	],
})
export class DisplayUserComponent {
	userInfo: Observable<user> = this.accountService.user;
	opened: boolean = false;
	authScheme = environment.authScheme;

	constructor(private accountService: UserDataAccountService) {}
}
