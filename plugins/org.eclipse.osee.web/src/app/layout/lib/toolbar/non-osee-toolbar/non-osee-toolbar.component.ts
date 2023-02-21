/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterOutlet } from '@angular/router';
import ToolbarComponent from '../toolbar.component';
import { DisplayUserComponent } from '../../../../user-display/display-user.component';

@Component({
	selector: 'osee-non-osee-toolbar',
	standalone: true,
	templateUrl: '../toolbar.component.html',
	styleUrls: ['../toolbar.component.sass'],
	imports: [
		MatToolbarModule,
		MatButtonModule,
		MatIconModule,
		RouterOutlet,
		NgIf,
		AsyncPipe,
		MatProgressSpinnerModule,
		DisplayUserComponent,
	],
})
export class NonOseeToolbarComponent extends ToolbarComponent {
	oseeToolbar = false;
	constructor() {
		super();
	}
}
export default NonOseeToolbarComponent;
