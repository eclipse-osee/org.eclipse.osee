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
import { Component, inject, signal } from '@angular/core';
import { filter, map } from 'rxjs';
import { SideNavService } from '@osee/shared/services/layout';
import { NavContainerComponent } from '@osee/layout/container';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { SnackbarWrapperComponent } from '@osee/shared/components';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { osee_logo } from './osee_logo';

@Component({
	selector: 'osee-root',
	templateUrl: './app.component.html',
	imports: [RouterOutlet, NavContainerComponent, SnackbarWrapperComponent],
})
export class AppComponent {
	private sideNavService = inject(SideNavService);
	private matIconRegistry = inject(MatIconRegistry);
	private domSanitizer = inject(DomSanitizer);
	private router = inject(Router);

	rightSideNavOpened = this.sideNavService.rightSideNavOpened;
	leftSideNavOpened = this.sideNavService.leftSideNav.pipe(
		map((v) => v.opened)
	);

	/** True when the current route is a popup (no app shell needed). */
	protected readonly isPopupMode = signal(
		window.location.pathname.includes('help-popup')
	);

	constructor() {
		this.matIconRegistry.addSvgIconLiteral(
			'osee_logo',
			this.domSanitizer.bypassSecurityTrustHtml(osee_logo)
		);

		// Also update on navigation in case of late detection
		this.router.events
			.pipe(
				filter((e) => e instanceof NavigationEnd),
				map((e) =>
					(e as NavigationEnd).urlAfterRedirects.startsWith(
						'/help-popup'
					)
				)
			)
			.subscribe((isPopup) => {
				if (isPopup) {
					this.isPopupMode.set(true);
				}
			});
	}

	toggleTopLevelNavIcon() {
		this.sideNavService.toggleLeftSideNav = '';
	}

	closeTopLevelNavIcon() {
		this.sideNavService.closeLeftSideNav = '';
	}

	title = 'OSEE';
}
