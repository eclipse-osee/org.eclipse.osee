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
	effect,
	input,
	model,
	output,
	signal,
	inject,
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
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import {
	ARTIFACTTYPEIDENUM,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';
import {
	createArtifact,
	modifyArtifact,
	modifyRelation,
} from '@osee/transactions/types';

@Component({
	selector: 'osee-enum-form',
	templateUrl: './enum-form.component.html',
	styles: [],
	standalone: true,
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
	private _addTxRows = computed(() => {
		return this.enums().map((v) => {
			return {
				typeId: ARTIFACTTYPEIDENUM.ENUM,
				name: v.name.value,
				key: crypto.randomUUID(),
				applicabilityId: v.applicability.id,
				attributes: [
					{
						typeId: ATTRIBUTETYPEIDENUM.INTERFACEENUMORDINAL,
						value: v.ordinal.value,
					},
				],
				relations: [
					{
						typeId: RELATIONTYPEIDENUM.INTERFACEENUMTOENUMSET,
						sideA:
							this.enumSetId() !== '-1'
								? this.enumSetId()
								: 'ea95f2e8-6018-4975-917d-5d49ce56151a', //random GUID that's hopefully unique enough for enum set
					},
				],
			};
		});
	});
	protected _modifyTxRows = computed(() => {
		return this.enums()
			.filter((v) => 'id' in v)
			.map((v) => {
				return {
					id: v.id || '-1',
					applicabilityId: v.applicability.id,
					setAttributes: [
						{
							typeId: ATTRIBUTETYPEIDENUM.INTERFACEENUMORDINAL,
							value: v.ordinal,
						},
						{
							typeId: ATTRIBUTETYPEIDENUM.NAME,
							value: v.name,
						},
					],
					relations: [
						{
							typeId: RELATIONTYPEIDENUM.INTERFACEENUMTOENUMSET,
							sideA:
								this.enumSetId() !== '-1'
									? this.enumSetId()
									: 'ea95f2e8-6018-4975-917d-5d49ce56151a', //random GUID that's hopefully unique enough for enum set
						},
					],
				};
			});
	});

	private deletedEnums = signal<enumeration[]>([]);

	private _deletedTxRows = computed(() => {
		return this.deletedEnums()
			.filter((v) => 'id' in v)
			.map((v) => {
				return {
					aArtId: this.enumSetId(),
					bArtId: v.id || '-1',
					typeId: RELATIONTYPEIDENUM.INTERFACEENUMTOENUMSET,
				};
			});
	});

	tx = output<{
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}>();

	private _updateTx = effect(
		() => {
			this.tx.emit({
				createArtifacts: this._addTxRows(),
				modifyArtifacts: this._modifyTxRows(),
				deleteRelations: this._deletedTxRows(),
			});
		},
		{ allowSignalWrites: true }
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
			applicability: { id: '1' as const, name: 'Base' },
		};
		this.enums.update((e) => [...e, newEnum]);
	}

	removeEnum(enumeration: enumeration) {
		this.enums.update((e) => e.filter((x) => x !== enumeration));
		this.deletedEnums.update((d) => [...d, enumeration]);
	}
}
