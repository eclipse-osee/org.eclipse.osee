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
	imports: [
		RouterOutlet,
		AsyncPipe,
		DisplayUserComponent,
		MatToolbar,
		MatIconButton,
		MatIcon,
		MatProgressSpinner,
	],
	styles: [],
	template: `<mat-toolbar class="tw-relative tw-h-[6vh] tw-w-screen">
		<div
			class="tw-flex tw-w-[85vw] tw-min-w-[70vw] tw-max-w-[85vw] tw-items-center tw-justify-between">
			<!-- Top Level Nav - Button -->
			<span
				class="tw-flex tw-flex-grow tw-items-center tw-justify-start tw-gap-4">
				<button
					mat-icon-button
					(click)="toggleTopLevelNavIcon()">
					<mat-icon>{{ topLevelNavIcon | async }}</mat-icon>
				</button>

				@if (oseeToolbar) {
					<router-outlet name="toolbarLogo"></router-outlet>
				}
				<router-outlet
					name="title"
					class="tw-flex-shrink tw-flex-grow"
					(click)="closeTopLevelNavIcon()"></router-outlet>
				<router-outlet
					name="navigationHeader"
					class="tw-flex-shrink tw-flex-grow"
					(click)="closeTopLevelNavIcon()"></router-outlet>
			</span>
		</div>
		<span class="tw-flex-auto"></span>
		<span
			class="tw-relative tw-flex tw-min-w-[5vw] tw-items-center tw-justify-end tw-gap-2">
			@if ((isLoading | async) === 'true') {
				<mat-progress-spinner
					mode="indeterminate"
					diameter="40"></mat-progress-spinner>
			}
			<osee-display-user
				(click)="closeTopLevelNavIcon()"></osee-display-user>
		</span>
	</mat-toolbar>`,
})
export class ToolbarComponent {
	private loadingService: HttpLoadingService = inject(HttpLoadingService);
	private sideNavService: SideNavService = inject(SideNavService);
	topLevelNavIcon = this.sideNavService.leftSideNav.pipe(map((v) => v.icon));
	isLoading = this.loadingService.isLoading;

	@Input() oseeToolbar = true;
	toggleTopLevelNavIcon() {
		this.sideNavService.toggleLeftSideNav = '';
	}

	closeTopLevelNavIcon() {
		this.sideNavService.closeLeftSideNav = '';
	}
}
export default ToolbarComponent;
