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
import { AsyncPipe, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import type { PlatformType } from '@osee/messaging/shared/types';
import { PlatformTypeActionsComponent } from '../platform-type-actions/platform-type-actions.component';

@Component({
	selector: 'osee-messaging-types-platform-type-card',
	templateUrl: './platform-type-card.component.html',
	styles: [],
	standalone: true,
	imports: [
		MatCardModule,
		NgIf,
		AsyncPipe,
		MatDialogModule,
		MatIconModule,
		MatButtonModule,
		PlatformTypeActionsComponent,
	],
})
export class PlatformTypeCardComponent {
	@Input() typeData!: PlatformType;
}
