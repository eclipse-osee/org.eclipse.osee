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
import { Component } from '@angular/core';
import { map } from 'rxjs';
import { SideNavService } from '@osee/shared/services/layout';
import { NavContainerComponent } from '@osee/layout/container';
import { RouterOutlet } from '@angular/router';

@Component({
	selector: 'osee-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.sass'],
	standalone: true,
	imports: [RouterOutlet, NavContainerComponent],
})
export class AppComponent {
	rightSideNavOpened = this.sideNavService.rightSideNavOpened;
	leftSideNavOpened = this.sideNavService.leftSideNav.pipe(
		map((v) => v.opened)
	);

	toggleTopLevelNavIcon() {
		this.sideNavService.toggleLeftSideNav = '';
	}

	closeTopLevelNavIcon() {
		this.sideNavService.closeLeftSideNav = '';
	}

	constructor(private sideNavService: SideNavService) {}
	title = 'OSEE';
}
