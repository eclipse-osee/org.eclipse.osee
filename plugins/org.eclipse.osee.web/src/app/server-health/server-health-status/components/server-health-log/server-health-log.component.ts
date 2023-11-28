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
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
	BehaviorSubject,
	combineLatest,
	shareReplay,
	switchMap,
	tap,
} from 'rxjs';
import { ServerHealthHttpService } from 'src/app/server-health/shared/services/server-health-http.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-server-health-log',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './server-health-log.component.html',
})
export class ServerHealthLogComponent {
	constructor(private serverHealthHttpService: ServerHealthHttpService) {}

	remoteHealthLog = this.serverHealthHttpService
		.getRemoteLog()
		.pipe(
			shareReplay({ bufferSize: 1, refCount: true }),
			takeUntilDestroyed()
		);
}
