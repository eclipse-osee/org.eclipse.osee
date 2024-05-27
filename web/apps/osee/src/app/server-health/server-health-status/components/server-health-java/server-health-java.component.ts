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
import { Component, inject } from '@angular/core';
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
import { shareReplay, tap } from 'rxjs';
import { ServerHealthHttpService } from '../../../shared/services/server-health-http.service';

@Component({
	selector: 'osee-server-health-java',
	standalone: true,
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
	templateUrl: './server-health-java.component.html',
})
export class ServerHealthJavaComponent {
	private serverHealthHttpService = inject(ServerHealthHttpService);

	displayedColumns: string[] = ['key', 'value'];
	dataSource = new MatTableDataSource<{
		key: string;
		value: string | string[];
	}>([]);

	remoteHealthJava = this.serverHealthHttpService.RemoteJava.pipe(
		tap((data) => {
			// Set the dataSource
			const healthJavaArray = Object.entries(data.healthJava).map(
				([key, value]) => ({ key, value })
			);
			this.dataSource = new MatTableDataSource(healthJavaArray);
		}),
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);

	isValueAnArray(value: unknown) {
		return Array.isArray(value);
	}
}
