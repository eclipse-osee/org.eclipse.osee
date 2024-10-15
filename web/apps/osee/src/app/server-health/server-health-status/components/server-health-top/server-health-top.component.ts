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
import { Component, inject } from '@angular/core';

import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { shareReplay } from 'rxjs';
import { ServerHealthHttpService } from '../../../shared/services/server-health-http.service';
import { AsyncPipe } from '@angular/common';

@Component({
	selector: 'osee-server-health-top',
	standalone: true,
	imports: [AsyncPipe],
	templateUrl: './server-health-top.component.html',
})
export class ServerHealthTopComponent {
	private serverHealthHttpService = inject(ServerHealthHttpService);

	remoteHealthTop = this.serverHealthHttpService.RemoteTop.pipe(
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);
}
