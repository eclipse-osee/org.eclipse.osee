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
import { Component, inject } from '@angular/core';
import { AsyncPipe, NgIf } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { DisplayUserComponent } from '../../../userdata/components/display-user/display-user.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { HttpLoadingService } from '../../../services/http-loading.service';
import { SideNavService } from '../../../shared-services/ui/side-nav.service';
import { map } from 'rxjs';

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
	toggleTopLevelNavIcon() {
		this.sideNavService.toggleLeftSideNav = '';
	}

	closeTopLevelNavIcon() {
		this.sideNavService.closeLeftSideNav = '';
	}
}

export default ToolbarComponent;
