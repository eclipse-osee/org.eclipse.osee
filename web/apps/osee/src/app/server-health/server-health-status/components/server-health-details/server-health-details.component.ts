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
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
	MatTableDataSource,
} from '@angular/material/table';
import {
	BehaviorSubject,
	combineLatest,
	shareReplay,
	switchMap,
	tap,
} from 'rxjs';
import { ServerHealthHttpService } from '../../../shared/services/server-health-http.service';

@Component({
	selector: 'osee-server-health-details',
	imports: [
		AsyncPipe,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
	],
	templateUrl: './server-health-details.component.html',
})
export class ServerHealthDetailsComponent {
	private serverHealthHttpService = inject(ServerHealthHttpService);

	@Input() set serverName(value: string) {
		if (value) {
			this._serverName.next(value);
		}
	}

	_serverName = new BehaviorSubject<string>('');

	displayedColumns: string[] = ['key', 'value'];
	dataSource = new MatTableDataSource<{
		key: string;
		value: string | string[];
	}>([]);

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
