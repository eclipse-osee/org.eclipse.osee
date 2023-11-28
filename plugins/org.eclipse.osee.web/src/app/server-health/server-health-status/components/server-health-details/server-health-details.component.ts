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
	map,
	shareReplay,
	switchMap,
	tap,
} from 'rxjs';
import { ServerHealthHttpService } from '../../../shared/services/server-health-http.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

@Component({
	selector: 'osee-server-health-details',
	standalone: true,
	imports: [CommonModule, MatTableModule],
	templateUrl: './server-health-details.component.html',
})
export class ServerHealthDetailsComponent {
	constructor(private serverHealthHttpService: ServerHealthHttpService) {}

	@Input() set serverName(value: string) {
		if (value) {
			this._serverName.next(value);
		}
	}

	_serverName = new BehaviorSubject<string>('');

	displayedColumns: string[] = ['key', 'value'];
	dataSource = new MatTableDataSource<{ key: string; value: unknown }>([]);

	remoteHealthDetails = combineLatest([this._serverName]).pipe(
		switchMap(([name]) =>
			this.serverHealthHttpService.getRemoteDetails(name)
		),
		tap((data) => {
			// Set the dataSource
			const healthDetailsArray = Object.entries(data.healthDetails).map(
				([key, value]) => ({ key, value })
			);
			this.dataSource = new MatTableDataSource(healthDetailsArray);
		}),
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);

	isValueAnArray(value: unknown) {
		return Array.isArray(value);
	}
}
