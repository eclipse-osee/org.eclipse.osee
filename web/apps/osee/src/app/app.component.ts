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
import {
	ArtifactChangeNotificationService,
	BranchChangeEventService,
	SelectedBranchLifecycleService,
	UserPresenceService,
} from '@osee/shared/services';
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
	private changeNotification = inject(ArtifactChangeNotificationService);
	private branchChangeEvent = inject(BranchChangeEventService);
	private selectedBranchLifecycle = inject(SelectedBranchLifecycleService);
	private userPresence = inject(UserPresenceService);

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

		// Start the always-on notification streams and the selected-branch lifecycle reactions
		// (rebaseline re-point, deleted/purged navigate-away + notice).
		this.changeNotification.initialize();
		this.branchChangeEvent.initialize();
		this.selectedBranchLifecycle.initialize();
		// Presence must run in every tab (not just presence-watching pages): the heartbeat is sent
		// by the SSE-connection leader, which may be on a page that never watches a context.
		this.userPresence.initialize();

		// Detect popup routes late (in case of a redirect) so the shell is hidden.
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
