/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { Injectable, inject } from '@angular/core';
import { ConnectionService } from '@osee/messaging/shared/services';
import {
	transportType,
	type ImportOption,
	type ImportSummary,
	getTransportTypeSentinel,
} from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import {
	BehaviorSubject,
	OperatorFunction,
	Subject,
	combineLatest,
	of,
} from 'rxjs';
import { filter, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { ImportHttpService } from './import-http.service';

@Injectable({
	providedIn: 'root',
})
export class ImportService {
	private uiService = inject(UiService);
	private importHttpService = inject(ImportHttpService);
	private connectionService = inject(ConnectionService);

	private _importFile$: BehaviorSubject<File | undefined> =
		new BehaviorSubject<File | undefined>(undefined);
	private _selectedImportOption$: BehaviorSubject<ImportOption> =
		new BehaviorSubject<ImportOption>({
			id: '-1',
			name: '',
			url: '',
			connectionRequired: false,
			transportTypeRequired: false,
		});
	private _importSuccess$: BehaviorSubject<boolean | undefined> =
		new BehaviorSubject<boolean | undefined>(undefined);
	private _importInProgress$: Subject<boolean> = new Subject<boolean>();
	private _selectedConnectionId = new BehaviorSubject<string>('');
	private _transportType = new BehaviorSubject<transportType>(
		getTransportTypeSentinel()
	);

	private _connections = this.branchId.pipe(
		filter((v) => v !== ''),
		switchMap((branchId) => this.connectionService.getConnections(branchId))
	);

	private _importSummary$ = combineLatest([
		this.branchId,
		this._selectedImportOption$,
		this._importFile$,
		this._importInProgress$,
		this._selectedConnectionId,
		this._transportType,
	]).pipe(
		filter(
			([
				_branchId,
				importOption,
				file,
				inProgress,
				_connectionId,
				_transportType,
			]) => importOption !== undefined && file !== undefined && inProgress
		),
		switchMap(
			([
				branchId,
				importOption,
				file,
				_inProgress,
				connectionId,
				transportType,
			]) => {
				if (file?.name.endsWith('.json')) {
					return this.importHttpService.getImportSummary(
						importOption!.url
							.replace('<branchId>', branchId)
							.replace('<connectionId>', connectionId),
						transportType.id,
						file.name,
						file
					);
				}
				if (
					file?.name.endsWith('.xlsx') ||
					file?.name.endsWith('.xls') ||
					file?.name.endsWith('.zip')
				) {
					return of(new FormData()).pipe(
						tap((formData) => {
							formData.append(
								'file',
								new Blob([file!]),
								file?.name
							);
						}),
						switchMap((formData) =>
							this.importHttpService.getImportSummary(
								importOption!.url.replace(
									'<branchId>',
									branchId
								),
								transportType.id,
								file.name,
								formData
							)
						)
					);
				}
				return of(undefined);
			}
		),
		tap(() => (this.ImportInProgress = false)),
		filter((v) => v !== undefined) as OperatorFunction<
			ImportSummary | undefined,
			ImportSummary
		>,
		shareReplay({ bufferSize: 1, refCount: true })
	);

	performImport() {
		combineLatest([this.branchId, this.importSummary])
			.pipe(
				take(1),
				switchMap(([branchId, summary]) =>
					this.importHttpService.performImport(branchId, summary)
				),
				tap((res) => {
					this.ImportSuccess = res.results.success;
					this.ImportInProgress = false;
				})
			)
			.subscribe();
	}

	reset() {
		this.ImportFile = undefined;
		this.ImportSuccess = undefined;
		this.SelectedImportOption = {
			id: '-1',
			name: '',
			url: '',
			connectionRequired: false,
			transportTypeRequired: false,
		};
		this.SelectedConnectionId = '';
		this.ImportInProgress = false;
		this.SelectedConnectionId = '';
	}

	get branchId() {
		return this.uiService.id;
	}

	set BranchId(value: string) {
		this.uiService.idValue = value;
	}

	get branchType() {
		return this.uiService.type;
	}

	get importFile() {
		return this._importFile$.asObservable();
	}

	set ImportFile(importFile: File | undefined) {
		this._importFile$.next(importFile);
	}

	get selectedImportOption() {
		return this._selectedImportOption$.asObservable();
	}

	set SelectedImportOption(importOption: ImportOption) {
		this._selectedImportOption$.next(importOption);
	}

	get selectedConnectionId() {
		return this._selectedConnectionId;
	}

	set SelectedConnectionId(id: string) {
		this._selectedConnectionId.next(id);
	}

	get transportType() {
		return this._transportType.asObservable();
	}

	set TransportType(value: transportType) {
		this._transportType.next(value);
	}

	get importSummary() {
		return this._importSummary$;
	}

	get importSuccess() {
		return this._importSuccess$;
	}

	set ImportSuccess(value: boolean | undefined) {
		this._importSuccess$.next(value);
	}

	get importInProgress() {
		return this._importInProgress$.asObservable();
	}

	set ImportInProgress(value: boolean) {
		this._importInProgress$.next(value);
	}

	get importOptions() {
		return this.importHttpService.getImportOptions();
	}

	get connections() {
		return this._connections;
	}
}
