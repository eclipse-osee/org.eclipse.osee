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
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { shareReplay } from 'rxjs';
import { ServerHealthHttpService } from '../../../shared/services/server-health-http.service';

@Component({
	selector: 'osee-server-health-top',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './server-health-top.component.html',
})
export class ServerHealthTopComponent {
	constructor(private serverHealthHttpService: ServerHealthHttpService) {}

	remoteHealthTop = this.serverHealthHttpService.RemoteTop.pipe(
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);
}
