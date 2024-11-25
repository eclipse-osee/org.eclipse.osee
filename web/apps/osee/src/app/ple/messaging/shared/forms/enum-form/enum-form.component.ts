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
import { AsyncPipe } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	Output,
	computed,
	inject,
	input,
	model,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
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
} from '@angular/material/table';
import { validateEnumLengthIsBelowMax } from '@osee/messaging/shared/functions';
import {
	EnumerationSetQuery,
	andDescriptionQuery,
} from '@osee/messaging/shared/query';
import { CurrentQueryService } from '@osee/messaging/shared/services';
import type { enumeration } from '@osee/messaging/shared/types';
import { of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

import { toObservable } from '@angular/core/rxjs-interop';
import { MatIcon } from '@angular/material/icon';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import { applic } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

@Component({
	selector: 'osee-enum-form',
	templateUrl: './enum-form.component.html',
	styles: [],
	imports: [
		FormsModule,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatFormField,
		MatLabel,
		MatInput,
		MatIcon,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatButton,
		MatIconButton,
		AsyncPipe,
		ApplicabilityDropdownComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnumFormComponent {
	private queryService = inject(CurrentQueryService);

	bitSize = input.required<string>();
	enumSetName = input.required<string>();

	enumSetId = input.required<string>();

	enums = model.required<enumeration[]>();
	protected tableData = toObservable(this.enums);

	private _enumString = computed(() =>
		this.enums()
			.map((x) => `${x.ordinal.value} = ${x.name.value}`)
			.join('\n')
	);

	private _enumString$ = toObservable(this._enumString);

	@Output() enumSetString = this._enumString$;
	@Output() unique = this._enumString$.pipe(
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

	// /*transaction logic:
	//  * If enum has an id and enum values !== previous values modifyArtifact
	//  * If enum does not have an id, create a new enum and put in addArtifact
	//  * If preload is not [] and preload contains enum.id but current enums does not, deleteRelation
	//  */

	validateEnumLengthIsBelowMax = computed(() =>
		validateEnumLengthIsBelowMax(
			this.enums().length,
			parseInt(this.bitSize())
		)
	);

	addEnum() {
		const newEnum = {
			id: '-1' as const,
			gammaId: '-1' as const,
			name: {
				id: '-1' as const,
				typeId: ATTRIBUTETYPEIDENUM.NAME,
				gammaId: '-1' as const,
				value: '',
			},
			ordinal: {
				id: '-1' as const,
				typeId: ATTRIBUTETYPEIDENUM.INTERFACEENUMORDINAL,
				gammaId: '-1' as const,
				value:
					(this.enums()[this.enums().length - 1]?.ordinal !==
					undefined
						? this.enums()[this.enums().length - 1].ordinal.value
						: -1) + 1,
			},
			ordinalType: {
				id: '-1' as const,
				typeId: ATTRIBUTETYPEIDENUM.INTERFACEENUMORDINALTYPE,
				gammaId: '-1' as const,
				value: 'LONG',
			},
			applicability: { id: '1' as const, name: 'Base' },
		};
		this.enums.update((e) => [...e, newEnum]);
	}

	removeEnum(enumeration: enumeration) {
		this.enums.update((e) => e.filter((x) => x !== enumeration));
	}

	protected updateName(value: string, index: number) {
		this.enums.update((e) => {
			const x: enumeration = e[index];
			x.name.value = value;
			const newArr = [...e];
			newArr[index] = x;
			return newArr;
		});
	}
	protected updateOrdinal(value: number, index: number) {
		this.enums.update((e) => {
			const x: enumeration = e[index];
			x.ordinal.value = value;
			const newArr = [...e];
			newArr[index] = x;
			return newArr;
		});
	}
	protected updateApplic(value: applic, index: number) {
		this.enums.update((e) => {
			const x: enumeration = e[index];
			x.applicability = value;
			const newArr = [...e];
			newArr[index] = x;
			return newArr;
		});
	}
}
