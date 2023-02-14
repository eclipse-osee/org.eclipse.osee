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
import {
	ApplicabilityListUIService,
	CurrentQueryService,
} from '@osee/messaging/shared/services';
import {
	EnumerationSetQuery,
	andDescriptionQuery,
} from '@osee/messaging/shared/types';
import type { enumeration } from '@osee/messaging/shared/types';
import { combineLatest, from, of, Subject } from 'rxjs';
import {
	switchMap,
	concatMap,
	map,
	reduce,
	filter,
	debounceTime,
} from 'rxjs/operators';
import { applic } from '@osee/shared/types/applicability';

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
		MatSelectModule,
		MatOptionModule,
		NgFor,
		AsyncPipe,
		MatButtonModule,
	],
})
export class EnumFormComponent implements OnChanges {
	applics = this.applicabilityService.applic;
	dataSource = new MatTableDataSource<enumeration>();

	@Input() bitSize: string = '0';
	@Input() enumSetName: string = '';
	@Input() preload: enumeration[] = [];

	@Output('enums') tableData = new Subject<enumeration[]>();
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
	constructor(
		private applicabilityService: ApplicabilityListUIService,
		private queryService: CurrentQueryService
	) {}
	ngOnChanges(changes: SimpleChanges): void {
		this._name.next(this.enumSetName);
		if (this.dataSource.data.length === 0 && this.preload.length !== 0) {
			this.dataSource.data = this.preload;
			this.update();
		}
	}

	compareApplics(o1: applic, o2: applic) {
		return o1?.id === o2?.id && o1?.name === o2?.name;
	}

	addEnum() {
		let enumData = [
			...this.dataSource.data,
			{
				name: '',
				ordinal:
					(this.dataSource.data[this.dataSource.data.length - 1]
						?.ordinal !== undefined
						? this.dataSource.data[this.dataSource.data.length - 1]
								.ordinal
						: -1) + 1,
				applicability: { id: '1', name: 'Base' },
			},
		];
		this.dataSource.data = enumData;
		this.update();
	}

	validateEnumLengthIsBelowMax() {
		return validateEnumLengthIsBelowMax(
			this.dataSource.data.length,
			parseInt(this.bitSize)
		);
	}
	update() {
		this.tableData.next(this.dataSource.data);
	}
}
