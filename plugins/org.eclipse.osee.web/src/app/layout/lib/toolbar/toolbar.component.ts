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
import { Component, inject, Input } from '@angular/core';
import { AsyncPipe, NgIf } from '@angular/common';
import { HttpLoadingService } from '@osee/shared/services/network';
import { map } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterOutlet } from '@angular/router';
import { DisplayUserComponent } from './user-display/display-user.component';
import { SideNavService } from '@osee/shared/services/layout';

@Component({
	selector: 'osee-toolbar',
	standalone: true,
	templateUrl: './toolbar.component.html',
	styleUrls: ['./toolbar.component.sass'],
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
export class ToolbarComponent {
	private loadingService: HttpLoadingService = inject(HttpLoadingService);
	private sideNavService: SideNavService = inject(SideNavService);
	topLevelNavIcon = this.sideNavService.leftSideNav.pipe(map((v) => v.icon));
	isLoading = this.loadingService.isLoading;

	@Input() oseeToolbar: boolean = true;
	toggleTopLevelNavIcon() {
		this.sideNavService.toggleLeftSideNav = '';
	}

	closeTopLevelNavIcon() {
		this.sideNavService.closeLeftSideNav = '';
	}
}
export default ToolbarComponent;
