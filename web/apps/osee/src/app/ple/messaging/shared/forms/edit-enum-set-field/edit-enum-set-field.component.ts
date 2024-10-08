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
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { AsyncPipe } from '@angular/common';
import {
	Component,
	Input,
	OnChanges,
	Output,
	SimpleChanges,
	inject,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
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
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import {
	EnumerationUIService,
	PreferencesUIService,
	TypesUIService,
} from '@osee/messaging/shared/services';
import type { PlatformType, enumeration } from '@osee/messaging/shared/types';
import { applic } from '@osee/applicability/types';
import {
	ARTIFACTTYPEIDENUM,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';
import {
	createArtifact,
	modifyArtifact,
	modifyRelation,
} from '@osee/transactions/types';
import { BehaviorSubject, Subject, combineLatest, iif, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { EnumFormComponent } from '../enum-form/enum-form.component';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';

@Component({
	selector: 'osee-edit-enum-set-field',
	templateUrl: './edit-enum-set-field.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		CdkTextareaAutosize,
		MatTable,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatColumnDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		AsyncPipe,
		EnumFormComponent,
		ApplicabilityDropdownComponent,
	],
})
export class EditEnumSetFieldComponent implements OnChanges {
	private enumSetService = inject(EnumerationUIService);
	private preferenceService = inject(PreferencesUIService);
	private typeService = inject(TypesUIService);

	/*
	 *@deprecated
	 */
	private _updateEnums = new BehaviorSubject<enumeration[]>([]);
	private _enumSetNameUpdate = new BehaviorSubject<string>('');
	private _enumSetDescriptionUpdate = new BehaviorSubject<string>('');
	private _enumSetApplicUpdate = new BehaviorSubject<applic>({
		id: '-1',
		name: 'Invalid',
	});
	inEditMode = this.preferenceService.inEditMode;

	@Input() editable = false;
	_editable = new BehaviorSubject<boolean>(false);

	//type enumset loading case 1: by id
	@Input() platformTypeId = '-1';
	private __platformTypeId = new BehaviorSubject<string>('');

	//type enumset loading case 2: by type
	@Input() platformType: PlatformType = new PlatformTypeSentinel();

	private __platformType = new BehaviorSubject<PlatformType>(
		new PlatformTypeSentinel()
	);

	@Input() shouldCreateNew = false;

	private _shouldCreateNew = new BehaviorSubject<boolean>(false);

	protected _type = combineLatest([
		this.__platformType,
		this.__platformTypeId,
	]).pipe(
		switchMap(([_type, id]) =>
			iif(
				() => _type !== undefined && _type.id !== '-1',
				of(_type),
				iif(
					() => id !== undefined && id !== '-1',
					this.typeService.getType(id as string),
					of(new PlatformTypeSentinel())
				)
			)
		)
	);

	/**
	 * This is the starting enumeration set that shouldn't change
	 */
	private _enumerationSet$ = this._type.pipe(
		filter((val) => val.id !== '-1'),
		switchMap((type) => this.enumSetService.getEnumSet(type?.id || '-1'))
	);

	protected _enumerationSet = combineLatest([
		this._enumerationSet$,
		this._shouldCreateNew,
	]).pipe(
		map(([enumSet, shouldCreateNew]) => {
			if (shouldCreateNew) {
				enumSet.id = '-1';
			}
			return enumSet;
		})
	);
	private _enumsTx = new Subject<{
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}>();

	@Output() enum$ = this._enumerationSet.pipe(
		switchMap((initial) =>
			combineLatest([
				this._enumSetNameUpdate,
				this._enumSetDescriptionUpdate,
				this._enumSetApplicUpdate,
			]).pipe(
				map(([name, description, applic]) => {
					if (name !== '' && initial.name.value !== name) {
						initial.name.value = name;
					}
					if (
						description !== '' &&
						initial.description.value !== description
					) {
						initial.description.value = description;
					}
					if (
						applic.id !== '-1' &&
						initial.applicability.id !== applic.id
					) {
						initial.applicability = applic;
					}
					return initial;
				})
			)
		)
	);
	/**
	 * If enumUpdated contains id === -1 or no id, create a new enum with key = ea95f2e8-6018-4975-917d-5d49ce56151a so that enums can reference it
	 * append artifact to createArtifacts
	 * else
	 * create a modify Artifact for all attributes and append to modifyArtifacts
	 */
	@Output() enumSetTx = this.enum$.pipe(
		switchMap((enumerationSet) =>
			this._enumsTx.pipe(
				switchMap((enumTx) =>
					enumerationSet.id === undefined ||
					enumerationSet.id === '-1'
						? combineLatest([
								this._type,
								this._shouldCreateNew,
							]).pipe(
								map(([type, shouldBeNew]) => {
									const addedArtifact = {
										typeId: ARTIFACTTYPEIDENUM.ENUMSET,
										name: enumerationSet?.name.value || '',
										key: 'ea95f2e8-6018-4975-917d-5d49ce56151a',
										applicabilityId:
											enumerationSet?.applicability.id,
										attributes: [
											{
												typeId: ATTRIBUTETYPEIDENUM.DESCRIPTION,
												value:
													enumerationSet?.description ||
													'',
											},
										],
										relations: [
											{
												typeId: RELATIONTYPEIDENUM.INTERFACEENUMSETTOPLATFORMTYPE,
												sideA: shouldBeNew
													? '3e1a2d30-f7db-43d2-af9d-d423115cbb8'
													: type.id || '-1',
											},
										],
									};
									if (
										enumTx.createArtifacts
											.map((v) => v.typeId)
											.includes(
												ARTIFACTTYPEIDENUM.ENUMSET
											)
									) {
										const index = enumTx.createArtifacts
											.map((v) => v.typeId)
											.indexOf(
												ARTIFACTTYPEIDENUM.ENUMSET
											);
										enumTx.createArtifacts[index] =
											addedArtifact;
									} else {
										enumTx.createArtifacts.push(
											addedArtifact
										);
									}
									return enumTx;
								})
							)
						: of(enumTx).pipe(
								map((existingTx) => {
									const modifiedArtifact = {
										id: enumerationSet!.id || '-1',
										applicabilityId:
											enumerationSet?.applicability.id ||
											'-1',
										setAttributes: [
											{
												typeId: ATTRIBUTETYPEIDENUM.NAME,
												value:
													enumerationSet?.name || '',
											},
											{
												typeId: ATTRIBUTETYPEIDENUM.DESCRIPTION,
												value:
													enumerationSet?.description ||
													'',
											},
										],
									};
									const index = existingTx.modifyArtifacts
										.map((v) => v.id)
										.indexOf(enumerationSet.id || '-1');
									if (index !== -1) {
										existingTx.modifyArtifacts.splice(
											index,
											1
										);
									}
									existingTx.modifyArtifacts.push(
										modifiedArtifact
									);
									return existingTx;
								})
							)
				)
			)
		)
	);

	@Output('unique') _unique = new Subject<boolean>();

	ngOnChanges(changes: SimpleChanges) {
		if (
			changes.platformType !== undefined &&
			(changes.platformType.previousValue !==
				changes.platformType.currentValue ||
				this.platformType !== changes.platformType.currentValue) &&
			changes.platformType.currentValue !== undefined
		) {
			this.__platformType.next(changes.platformType.currentValue);
		}

		if (
			changes.platformTypeId &&
			(changes.platformTypeId.previousValue !==
				changes.platformTypeId.currentValue ||
				this.platformTypeId !== changes.platformTypeId.currentValue) &&
			changes.platformTypeId.currentValue !== undefined
		) {
			this.__platformTypeId.next(changes.platformTypeId.currentValue);
		}
		if (
			(changes.editable.previousValue !== changes.editable.currentValue ||
				this.editable !== changes.editable.currentValue) &&
			changes.editable.currentValue !== undefined
		) {
			this._editable.next(changes.editable.currentValue);
		}

		if (
			changes.shouldCreateNew !== undefined &&
			(changes.shouldCreateNew.previousValue !==
				changes.shouldCreateNew.currentValue ||
				this.shouldCreateNew !==
					changes.shouldCreateNew.currentValue) &&
			changes.shouldCreateNew.currentValue !== undefined
		) {
			this._shouldCreateNew.next(changes.shouldCreateNew.currentValue);
		}
	}

	setName(value: string) {
		this._enumSetNameUpdate.next(value);
	}
	setApplicability(value: applic) {
		this._enumSetApplicUpdate.next(value);
	}

	setDescription(value: string) {
		this._enumSetDescriptionUpdate.next(value);
	}

	updateUnique(value: boolean) {
		this._unique.next(value);
	}

	updateEnumTx(value: unknown) {
		this._enumsTx.next(
			value as {
				createArtifacts: {
					typeId: typeof ARTIFACTTYPEIDENUM.ENUM;
					name: string;
					applicabilityId: string;
					attributes: {
						typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEENUMORDINAL;
						value: number;
					}[];
					key: string;
					relations: {
						typeId: typeof RELATIONTYPEIDENUM.INTERFACEENUMTOENUMSET;
						sideA: string;
					}[];
				}[];
				modifyArtifacts: {
					id: string;
					applicabilityId: string;
					setAttributes: {
						typeId:
							| typeof ATTRIBUTETYPEIDENUM.NAME
							| typeof ATTRIBUTETYPEIDENUM.INTERFACEENUMORDINAL;
						value: string | number;
					}[];
				}[];
				deleteRelations: {
					aArtId: string;
					bArtId: string;
					typeId: typeof RELATIONTYPEIDENUM.INTERFACEENUMTOENUMSET;
				}[];
			}
		);
	}
}
