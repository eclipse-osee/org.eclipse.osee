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
import { AsyncPipe } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { RouterOutlet } from '@angular/router';
import { SideNavService } from '@osee/shared/services/layout';
import { map } from 'rxjs';

@Component({
	selector: 'osee-nav-container',
	standalone: true,
	templateUrl: './nav-container.component.html',
	styles: [],
	imports: [MatSidenavModule, RouterOutlet, AsyncPipe],
})
export class NavContainerComponent {
	private sideNavService: SideNavService = inject(SideNavService);
	rightSideNavOpened = this.sideNavService.rightSideNavOpened;
	leftSideNavOpened = this.sideNavService.leftSideNav.pipe(
		map((v) => v.opened)
	);
	closeLeftLevelNav() {
		this.sideNavService.closeLeftSideNav = '';
	}
}

export default NavContainerComponent;
