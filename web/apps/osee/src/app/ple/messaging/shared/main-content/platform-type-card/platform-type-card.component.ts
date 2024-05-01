/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { AsyncPipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MatCard,
	MatCardActions,
	MatCardContent,
	MatCardHeader,
	MatCardSubtitle,
	MatCardTitle,
	MatCardTitleGroup,
} from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import type { PlatformType } from '@osee/messaging/shared/types';
import { PlatformTypeActionsComponent } from '../platform-type-actions/platform-type-actions.component';

@Component({
	selector: 'osee-messaging-types-platform-type-card',
	templateUrl: './platform-type-card.component.html',
	styles: [],
	standalone: true,
	imports: [
		AsyncPipe,
		MatCard,
		MatCardHeader,
		MatCardTitleGroup,
		MatCardTitle,
		MatCardSubtitle,
		MatCardContent,
		MatCardActions,
		MatButton,
		MatIcon,
		PlatformTypeActionsComponent,
	],
})
export class PlatformTypeCardComponent {
	@Input() typeData!: PlatformType;
}
