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
import { AsyncPipe, NgFor } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	Input,
	OnChanges,
	Output,
	SimpleChanges,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { validateEnumLengthIsBelowMax } from '@osee/messaging/shared/functions';
import { CurrentQueryService } from '@osee/messaging/shared/services';
import {
	EnumerationSetQuery,
	andDescriptionQuery,
} from '@osee/messaging/shared/query';
import type { enumeration } from '@osee/messaging/shared/types';
import { BehaviorSubject, combineLatest, from, iif, of, Subject } from 'rxjs';
import {
	switchMap,
	concatMap,
	map,
	reduce,
	filter,
	debounceTime,
	scan,
	startWith,
} from 'rxjs/operators';

import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import { MatIconModule } from '@angular/material/icon';
import {
	ARTIFACTTYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';
import { createArtifact } from '@osee/shared/types';

@Component({
	selector: 'osee-enum-form',
	templateUrl: './enum-form.component.html',
	styleUrls: ['./enum-form.component.sass'],
	standalone: true,
	imports: [
		MatTableModule,
		MatFormFieldModule,
		FormsModule,
		MatInputModule,
		MatIconModule,
		MatSelectModule,
		MatOptionModule,
		NgFor,
		AsyncPipe,
		MatButtonModule,
		ApplicabilitySelectorComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnumFormComponent implements OnChanges {
	dataSource = new MatTableDataSource<enumeration>();

	@Input() bitSize: string = '0';
	@Input() enumSetName: string = '';

	@Input() enumSetId: string = '';
	private _enumSetId$ = new BehaviorSubject<string>('');

	@Input() preload: enumeration[] = [];
	private _preload = new BehaviorSubject<enumeration[]>([]);

	@Output('enums') tableData = new BehaviorSubject<enumeration[]>([]);
	private _name = new Subject<string>();

	@Output() enumSetString = combineLatest([this._name, this.tableData]).pipe(
		switchMap(([name, data]) =>
			of(data).pipe(
				concatMap((dataArray) =>
					from(dataArray).pipe(
						filter((enumeration) => enumeration.name !== ''),
						map(
							(enumeration) =>
								`${enumeration.ordinal} = ${enumeration.name}`
						)
					)
				),
				reduce((acc, curr) => [...acc, curr], [] as string[]),
				map((value) => value.join('\n'))
			)
		),
		debounceTime(500)
	);
	@Output() unique = this.enumSetString.pipe(
		switchMap((description) =>
			of(description).pipe(
				switchMap((description) =>
					of(
						new EnumerationSetQuery(undefined, [
							new andDescriptionQuery(description),
						])
					).pipe(
						switchMap((query) =>
							this.queryService.queryExact(query)
						)
					)
				)
			)
		),
		map((results) => (results.length > 0 ? false : true))
	);

	private _addTxRows = combineLatest([this.tableData, this._enumSetId$]).pipe(
		debounceTime(500),
		switchMap((table) =>
			of(table).pipe(
				concatMap(([table, enumSetId]) =>
					from(table).pipe(
						filter(
							(row) =>
								((row.id === undefined ||
									row.id === null ||
									row.id === '-1' ||
									!row.id) &&
									row.name !== '' &&
									enumSetId !== '-1') ||
								enumSetId === '' ||
								enumSetId === '-1'
						),
						map((row) => {
							return {
								typeId: ARTIFACTTYPEIDENUM.ENUM,
								name: row.name,
								applicabilityId: row.applicability.id,
								attributes: [
									{
										typeId: ATTRIBUTETYPEIDENUM.INTERFACEENUMORDINAL,
										value: row.ordinal,
									},
								],
								relations: [
									{
										typeId: RELATIONTYPEIDENUM.INTERFACEENUMTOENUMSET,
										sideA:
											this.enumSetId !== '' &&
											this.enumSetId !== '-1'
												? this.enumSetId
												: 'ea95f2e8-6018-4975-917d-5d49ce56151a', //random GUID that's hopefully unique enough for enum set
									},
								],
							};
						}),
						scan(
							(acc, curr) => [...acc, curr],
							[] as {
								typeId: typeof ARTIFACTTYPEIDENUM.ENUM;
								name: string;
								applicabilityId: string;
								attributes: {
									typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEENUMORDINAL;
									value: number;
								}[];
								relations: {
									typeId: typeof RELATIONTYPEIDENUM.INTERFACEENUMTOENUMSET;
									sideA: string;
								}[];
							}[]
						),
						startWith([])
					)
				)
			)
		)
	);

	private _modifyTxRows = this._preload.pipe(
		switchMap((existing) =>
			this.tableData.pipe(
				debounceTime(500),
				switchMap((table) =>
					iif(
						() => existing.length > 0,
						from(table).pipe(
							//filter out data that isn't available in the preload whatsoever
							filter(
								(row) =>
									row.id !== undefined &&
									row.id !== null &&
									row.id !== '-1' &&
									existing.map((e) => e.id).includes(row.id)
							),

							//filter out objects that have no change from the preload
							filter((row) => {
								const index = existing
									.map((e) => e.id)
									.indexOf(row.id);
								const nameMatch =
									existing[index].name !== row.name;
								const applicabilityMatch =
									existing[index].applicability.id !==
									row.applicability.id;
								const ordinalMatch =
									existing[index].ordinal !== row.ordinal;
								return (
									nameMatch ||
									applicabilityMatch ||
									ordinalMatch
								);
							}),
							map((row) => {
								return {
									id: row.id || '-1',
									applicabilityId: row.applicability.id,
									setAttributes: [
										{
											typeId: ATTRIBUTETYPEIDENUM.INTERFACEENUMORDINAL,
											value: row.ordinal,
										},
										{
											typeId: ATTRIBUTETYPEIDENUM.NAME,
											value: row.name,
										},
									],
								};
							}),
							scan(
								(acc, curr) => [...acc, curr],
								[] as {
									id: string;
									applicabilityId: string;
									setAttributes: {
										typeId:
											| typeof ATTRIBUTETYPEIDENUM.NAME
											| typeof ATTRIBUTETYPEIDENUM.INTERFACEENUMORDINAL;
										value: string | number;
									}[];
								}[]
							),
							startWith([])
						),
						of([])
					)
				)
			)
		)
	);

	private _preloadRows = this._preload.pipe(
		concatMap((existing) => from(existing))
	);
	private _deleteTxRows = this.tableData.pipe(
		switchMap((modifiedData) =>
			combineLatest([this._preload, this._enumSetId$]).pipe(
				switchMap(([existing, _enumSetId]) =>
					iif(
						() =>
							existing.length > 0 &&
							_enumSetId !== '' &&
							_enumSetId !== '-1',
						this._preloadRows.pipe(
							filter(
								(existingRow) =>
									!modifiedData
										.map((v) => v.id)
										.includes(existingRow.id) &&
									existingRow.id !== '-1' &&
									existingRow.id !== undefined &&
									existingRow.id !== null
							),
							map((existingRow) => {
								return {
									aArtId: _enumSetId,
									bArtId: existingRow.id || '-1',
									typeId: RELATIONTYPEIDENUM.INTERFACEENUMTOENUMSET,
								};
							}),
							scan(
								(acc, curr) => [...acc, curr],
								[] as {
									aArtId: string;
									bArtId: string;
									typeId: typeof RELATIONTYPEIDENUM.INTERFACEENUMTOENUMSET;
								}[]
							),
							startWith([])
						),
						of([])
					)
				)
			)
		)
	);
	/*transaction logic:
	 * If enum has an id and enum values !== previous values modifyArtifact
	 * If enum does not have an id, create a new enum and put in addArtifact
	 * If preload is not [] and preload contains enum.id but current enums does not, deleteRelation
	 */

	@Output() tx = combineLatest([
		this._addTxRows,
		this._modifyTxRows,
		this._deleteTxRows,
	]).pipe(
		switchMap(([add, modify, deleted]) =>
			of({
				createArtifacts: add,
				modifyArtifacts: modify,
				deleteRelations: deleted,
			})
		)
	);

	constructor(private queryService: CurrentQueryService) {}
	ngOnChanges(changes: SimpleChanges): void {
		this._name.next(this.enumSetName);
		if (
			this.dataSource.data.length === 0 &&
			this.preload.length !== 0 &&
			changes.preload.previousValue === undefined
		) {
			this.dataSource.data = JSON.parse(
				JSON.stringify(changes.preload.currentValue.slice())
			);
			this._preload.next(
				JSON.parse(JSON.stringify(changes.preload.currentValue.slice()))
			);
			this._enumSetId$.next(this.enumSetId);
			this.update();
		}
	}

	addEnum() {
		const newEnum = {
			name: '',
			ordinal:
				(this.dataSource.data[this.dataSource.data.length - 1]
					?.ordinal !== undefined
					? this.dataSource.data[this.dataSource.data.length - 1]
							.ordinal
					: -1) + 1,
			applicability: { id: '1', name: 'Base' },
		};
		let enumData = [...this.tableData.getValue(), newEnum];
		this.dataSource.data = enumData;
		this.update(newEnum, undefined, true);
	}

	removeEnum(rowId: number) {
		const preSlice = this.tableData.getValue();
		const index = preSlice.map((v) => v.id).indexOf(rowId.toString());
		preSlice.splice(index, 1);
		this.tableData.next(preSlice.slice());
		this.dataSource.data = preSlice.slice();
	}

	validateEnumLengthIsBelowMax() {
		return validateEnumLengthIsBelowMax(
			this.dataSource.data.length,
			parseInt(this.bitSize)
		);
	}
	update(enumeration?: enumeration, rowId?: number, isNew?: boolean) {
		if (enumeration) {
			//update the values in the data source
			const internalIndex = this.dataSource.data
				.map((v) => v.id)
				.indexOf(enumeration.id);
			let data = this.tableData.getValue();
			if ((internalIndex === -1 || rowId === undefined) && isNew) {
				//brand new enum
				data.push(enumeration);
			} else if (rowId === undefined) {
				//existing enums
				data[internalIndex] = enumeration;
			} else {
				//edit new enum
				data[rowId] = enumeration;
			}
			this.tableData.next(data);
			//this.dataSource.data = data;
		} else {
			this.tableData.next(this.dataSource.data.slice());
		}
	}
}
