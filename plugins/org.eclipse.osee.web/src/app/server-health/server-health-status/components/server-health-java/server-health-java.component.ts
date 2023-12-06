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
import { shareReplay, tap } from 'rxjs';
import { ServerHealthHttpService } from '../../../shared/services/server-health-http.service';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

@Component({
	selector: 'osee-server-health-java',
	standalone: true,
	imports: [CommonModule, MatTableModule],
	templateUrl: './server-health-java.component.html',
})
export class ServerHealthJavaComponent {
	constructor(private serverHealthHttpService: ServerHealthHttpService) {}

	displayedColumns: string[] = ['key', 'value'];
	dataSource = new MatTableDataSource<{ key: string; value: unknown }>([]);

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
