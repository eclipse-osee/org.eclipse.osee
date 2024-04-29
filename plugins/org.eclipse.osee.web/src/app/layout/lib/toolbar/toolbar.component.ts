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
import { AsyncPipe } from '@angular/common';
import { Component, Input, inject } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatToolbar } from '@angular/material/toolbar';
import { RouterOutlet } from '@angular/router';
import { SideNavService } from '@osee/shared/services/layout';
import { HttpLoadingService } from '@osee/shared/services/network';
import { map } from 'rxjs';
import { DisplayUserComponent } from './user-display/display-user.component';

@Component({
	selector: 'osee-toolbar',
	standalone: true,
	templateUrl: './toolbar.component.html',
	styles: [],
	imports: [
		RouterOutlet,
		AsyncPipe,
		DisplayUserComponent,
		MatToolbar,
		MatIconButton,
		MatIcon,
		MatProgressSpinner,
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
