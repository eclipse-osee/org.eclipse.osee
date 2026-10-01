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
import { Component, inject } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterOutlet } from '@angular/router';
import { UserDataAccountService } from '@osee/auth';
import { SseEventService } from '@osee/shared/services/network';
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
	imports: [
		RouterOutlet,
		AsyncPipe,
		OktaSignComponent,
		MatMenuTrigger,
		MatIcon,
		MatMenu,
		MatMenuItem,
		MatTooltip,
	],
})
export class DisplayUserComponent {
	private accountService = inject(UserDataAccountService);
	private sseEventService = inject(SseEventService);

	userInfo: Observable<user> = this.accountService.user;
	opened = false;
	authScheme = environment.authScheme;

	/** True when the user could not be resolved (auth failed / server unavailable). */
	protected isSignedOut(u: user | null | undefined): boolean {
		return !u || u.id === '-1';
	}

	/** SSE connection state for badge and dropdown display. */
	protected connectionState = this.sseEventService.connectionState;

	/** Manually forces an immediate SSE reconnect attempt (leader-routed inside the service). */
	protected reconnectNow() {
		this.sseEventService.reconnectNow();
	}
}
