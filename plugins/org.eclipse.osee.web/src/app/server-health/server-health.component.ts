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
import { Component, SecurityContext } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { navigationStructure } from '@osee/layout/routing';
import { MatGridListModule } from '@angular/material/grid-list';
import {
	defaultNavigationElement,
	navigationElement,
} from '@osee/shared/types';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { ServerHealthPageHeaderComponent } from './shared/components/server-health-page-header/server-health-page-header.component';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ServerHealthHttpService } from './shared/services/server-health-http.service';
import { map, of, shareReplay } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatTooltipModule } from '@angular/material/tooltip';

const _navItems: navigationElement[] =
	navigationStructure[1].children.filter(
		(c) => c.label !== 'Server Health Dashboard'
	) || [];

const _currNavItem: navigationElement =
	navigationStructure[1].children.find(
		(c) => c.label === 'Server Health Dashboard'
	) || defaultNavigationElement;

@Component({
	selector: 'osee-server-health',
	standalone: true,
	imports: [
		CommonModule,
		MatIconModule,
		MatGridListModule,
		MatButtonModule,
		RouterLink,
		ServerHealthPageHeaderComponent,
		MatTooltipModule,
	],
	templateUrl: './server-health.component.html',
})
export class ServerHealthComponent {
	get importantNavItems() {
		return _navItems.filter((c) => c.label !== 'Http Headers');
	}

	get nonImportantNavItems() {
		return _navItems.filter((c) => c.label === 'Http Headers');
	}

	get currNavItem() {
		return _currNavItem;
	}

	constructor(
		private serverHealthHttpService: ServerHealthHttpService,
		private sanitizer: DomSanitizer
	) {}

	prometheusUrl = this.serverHealthHttpService.getPrometheusUrl().pipe(
		map((prometheusUrl) => {
			const urlWithParameters = prometheusUrl;
			return this.sanitizer.bypassSecurityTrustResourceUrl(
				urlWithParameters
			);
		})
	);

	openLink(safeUrl: SafeResourceUrl) {
		const url = this.sanitizer.sanitize(
			SecurityContext.URL,
			safeUrl
		) as string;
		window.open(url, '_blank');
	}

	activeMq = this.serverHealthHttpService
		.getActiveMq()
		.pipe(
			shareReplay({ bufferSize: 1, refCount: true }),
			takeUntilDestroyed()
		);
}
export default ServerHealthComponent;
