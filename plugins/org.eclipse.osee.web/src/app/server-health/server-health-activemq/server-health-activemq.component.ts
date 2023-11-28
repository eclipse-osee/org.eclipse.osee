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
import { AsyncPipe } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ServerHealthHttpService } from '../shared/services/server-health-http.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { shareReplay } from 'rxjs';
import { ServerHealthPageHeaderComponent } from '../shared/components/server-health-page-header/server-health-page-header.component';
import { navigationStructure } from '@osee/layout/routing';
import {
	navigationElement,
	defaultNavigationElement,
} from '@osee/shared/types';
import { MatIconModule } from '@angular/material/icon';

const _currNavItem: navigationElement =
	navigationStructure[1].children.find((c) => c.label === 'ActiveMQ') ||
	defaultNavigationElement;

@Component({
	selector: 'osee-server-health-activemq',
	standalone: true,
	imports: [AsyncPipe, ServerHealthPageHeaderComponent, MatIconModule],
	templateUrl: './server-health-activemq.component.html',
})
export class ServerHealthActivemqComponent {
	constructor(
		private serverHealthHttpService: ServerHealthHttpService,
		private sanitizer: DomSanitizer
	) {}

	get currNavItem() {
		return _currNavItem;
	}

	activeMq = this.serverHealthHttpService
		.getActiveMq()
		.pipe(
			shareReplay({ bufferSize: 1, refCount: true }),
			takeUntilDestroyed()
		);

	sanitizeUrl(url: string) {
		return this.sanitizer.bypassSecurityTrustResourceUrl(url);
	}

	openLink(safeUrl: SafeResourceUrl) {
		const url = this.sanitizer.sanitize(
			SecurityContext.URL,
			safeUrl
		) as string;
		window.open(url, '_blank');
	}
}
export default ServerHealthActivemqComponent;
