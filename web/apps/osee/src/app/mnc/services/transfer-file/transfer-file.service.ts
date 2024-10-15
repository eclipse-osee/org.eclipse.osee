/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { apiURL } from '@osee/environments';
import { results } from '../../types/transfer-file/results';
import {
	BehaviorSubject,
	Observable,
	Subject,
	filter,
	iif,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs';
import { UiService } from '@osee/shared/services';
import { toObservable } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class TransferFileService {
	protected http = inject(HttpClient);
	id = new BehaviorSubject<string>('');
	uiService = inject(UiService);
	onEnter = new Subject<void>();
	sourceId = new BehaviorSubject<string>('');
	exportId = signal<string>('');
	_exportId = toObservable(this.exportId);
	_export = this.sourceId.pipe(
		filter((val) => val != ''),
		take(1),
		switchMap((val) =>
			this.generateExport(val).pipe(
				tap((_) => (this.uiService.updated = true))
			)
		)
	);
	_exportData = this._exportId.pipe(
		switchMap((id) =>
			iif(
				() => id != '',
				this.onEnter.pipe(switchMap((_) => this.getDataWithId(id))),
				this.getData()
			)
		),
		shareReplay({ refCount: true, bufferSize: 1 })
	);
	get exportData() {
		return this._exportData;
	}
	getData() {
		return this.http.get<results>(apiURL + '/orcs/txs/xfer/exportData');
	}
	getDataWithId(id: string) {
		return this.http.get<results>(
			apiURL + '/orcs/txs/xfer/exportData?exportId=' + id
		);
	}
	generateExport(sourceId: string): Observable<results> {
		return this.http.get<results>(
			apiURL + '/orcs/txs/xfer/getXferFile?exportId=' + sourceId
		);
	}
	downloadFile(filename: string): Observable<HttpResponse<Blob>> {
		return this.http.get(
			apiURL + '/orcs/txs/xfer/download?filename=' + filename,
			{ observe: 'response', responseType: 'blob' }
		);
	}
}
