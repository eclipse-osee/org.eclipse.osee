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
import { filter, map } from 'rxjs';
import { SideNavService, FaviconService } from '@osee/shared/services/layout';
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

	rightSideNavOpened = this.sideNavService.rightSideNavOpened;
	leftSideNavOpened = this.sideNavService.leftSideNav.pipe(
		map((v) => v.opened)
	);

	constructor(
		private router: Router,
		private faviconService: FaviconService
	) {
		this.matIconRegistry.addSvgIconLiteral(
			'osee_logo',
			this.domSanitizer.bypassSecurityTrustHtml(osee_logo)
		);
	}

	ngOnInit() {
		this.router.events
			.pipe(filter((event) => event instanceof NavigationEnd))
			.subscribe((event: NavigationEnd) => {
				this.updateFavicon(event.url);
			});
	}

	toggleTopLevelNavIcon() {
		this.sideNavService.toggleLeftSideNav = '';
	}

	closeTopLevelNavIcon() {
		this.sideNavService.closeLeftSideNav = '';
	}

	private updateFavicon(url: string) {
		switch (true) {
			case url.includes('/messaging'):
				this.faviconService.setFavicon('assets/icons/polyline.ico');
				break;
			case url.includes('/explorer'):
				this.faviconService.setFavicon(
					'assets/icons/travel_explore.ico'
				);
				break;
			case url.includes('/plconfig'):
				this.faviconService.setFavicon('assets/icons/widgets.ico');
				break;
			case url.includes('/ci/'):
				this.faviconService.setFavicon('assets/icons/landscape.ico');
				break;
			case url.includes('/server/health'):
				this.faviconService.setFavicon(
					'assets/icons/monitor_heart.ico'
				);
				break;
			// Add more cases for different routes
			default:
				this.faviconService.resetFavicon();
				break;
		}
	}
}
