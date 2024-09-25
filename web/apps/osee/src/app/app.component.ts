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
import { Component, inject } from '@angular/core';
import { map } from 'rxjs';
import { SideNavService } from '@osee/shared/services/layout';
import { NavContainerComponent } from '@osee/layout/container';
import { RouterOutlet } from '@angular/router';
import { SnackbarWrapperComponent } from '@osee/shared/components';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { osee_logo } from './osee_logo';

@Component({
	selector: 'osee-root',
	templateUrl: './app.component.html',
	standalone: true,
	imports: [RouterOutlet, NavContainerComponent, SnackbarWrapperComponent],
})
export class AppComponent {
	private sideNavService = inject(SideNavService);
	private matIconRegistry = inject(MatIconRegistry);
	private domSanitizer = inject(DomSanitizer);

	rightSideNavOpened = this.sideNavService.rightSideNavOpened;
	leftSideNavOpened = this.sideNavService.leftSideNav.pipe(
		map((v) => v.opened)
	);

	constructor() {
		this.matIconRegistry.addSvgIconLiteral(
			'osee_logo',
			this.domSanitizer.bypassSecurityTrustHtml(osee_logo)
		);
	}

	toggleTopLevelNavIcon() {
		this.sideNavService.toggleLeftSideNav = '';
	}

	closeTopLevelNavIcon() {
		this.sideNavService.closeLeftSideNav = '';
	}

	title = 'OSEE';
}
