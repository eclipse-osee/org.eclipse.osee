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
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpLoadingService } from '../../../services/http-loading.service';
import { SideNavService } from '../../../shared-services/ui/side-nav.service';
import { map } from 'rxjs';

@Component({
	selector: 'osee-toolbar',
	standalone: true,
	imports: [CommonModule],
	template: ``,
	styles: [],
})
export class ToolbarComponent {
	private loadingService: HttpLoadingService = inject(HttpLoadingService);
	private sideNavService: SideNavService = inject(SideNavService);
	topLevelNavIcon = this.sideNavService.leftSideNav.pipe(map((v) => v.icon));
	isLoading = this.loadingService.isLoading;

	oseeToolbar: boolean = true;
	toggleTopLevelNavIcon() {
		this.sideNavService.toggleLeftSideNav = '';
	}

	closeTopLevelNavIcon() {
		this.sideNavService.closeLeftSideNav = '';
	}
}
export default ToolbarComponent;
