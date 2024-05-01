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
import { Component } from '@angular/core';

import { AsyncPipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { navigationStructure } from '@osee/layout/routing';
import {
	defaultNavigationElement,
	navigationElement,
} from '@osee/shared/types';
import { shareReplay } from 'rxjs';
import { ServerHealthPageHeaderComponent } from '../shared/components/server-health-page-header/server-health-page-header.component';
import { ServerHealthHttpService } from '../shared/services/server-health-http.service';
import { unknownJson } from '../shared/types/server-health-types';

const _currNavItem: navigationElement =
	navigationStructure[1].children.find((c) => c.label === 'Http Headers') ||
	defaultNavigationElement;

@Component({
	selector: 'osee-server-health-headers',
	standalone: true,
	imports: [AsyncPipe, ServerHealthPageHeaderComponent],
	templateUrl: './server-health-headers.component.html',
})
export class ServerHealthHeadersComponent {
	get currNavItem() {
		return _currNavItem;
	}

	constructor(private serverHealthHttpService: ServerHealthHttpService) {}

	headers = this.serverHealthHttpService.HttpHeaders.pipe(
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);

	getObjectKeys(obj: unknownJson): string[] {
		return Object.keys(obj).sort();
	}
}
export default ServerHealthHeadersComponent;
