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
import { Component, SecurityContext } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { navigationStructure } from '@osee/layout/routing';
import {
	defaultNavigationElement,
	navigationElement,
} from '@osee/shared/types';
import { map, shareReplay } from 'rxjs';
import { ServerHealthPageHeaderComponent } from './shared/components/server-health-page-header/server-health-page-header.component';
import { ServerHealthHttpService } from './shared/services/server-health-http.service';

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
		AsyncPipe,
		RouterLink,
		ServerHealthPageHeaderComponent,
		MatTooltip,
		MatIcon,
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

	prometheusUrl = this.serverHealthHttpService.PrometheusUrl.pipe(
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

	activeMq = this.serverHealthHttpService.ActiveMq.pipe(
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);
}
export default ServerHealthComponent;
