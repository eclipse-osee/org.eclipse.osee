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
import { Component, Inject, OnDestroy } from '@angular/core';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { BehaviorSubject, combineLatest, from, of, Subject } from 'rxjs';
import {
	concatMap,
	filter,
	map,
	reduce,
	shareReplay,
	switchMap,
	take,
	takeUntil,
} from 'rxjs/operators';
import {
	ARTIFACTTYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
} from '@osee/shared/types/constants';
import { AsyncPipe, NgIf } from '@angular/common';
import { MatStepperModule } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { A11yModule } from '@angular/cdk/a11y';
import { MatButtonModule } from '@angular/material/button';
import {
	UniquePlatformTypeNameDirective,
	UniquePlatformTypeAttributesDirective,
} from '@osee/messaging/shared/directives';
import type {
	logicalType,
	enumerationSet,
	editPlatformTypeDialogData,
	enumeratedPlatformType,
	PlatformType,
} from '@osee/messaging/shared/types';
import {
	editPlatformTypeDialogDataMode,
	PlatformTypeSentinel,
} from '@osee/messaging/shared/enumerations';
import { EditEnumSetFieldComponent } from '@osee/messaging/shared/forms';
import { TypesService } from '@osee/messaging/shared/services';
import {
	ApplicabilitySelectorComponent,
	MatOptionLoadingComponent,
} from '@osee/shared/components';
import { ParentErrorStateMatcher } from '@osee/shared/matchers';
import {
	createArtifact,
	modifyArtifact,
	modifyRelation,
} from '@osee/shared/types';
import {
	MIMATTRIBUTETYPEID,
	PLATFORMTYPEATTRIBUTETYPEID,
} from '@osee/messaging/shared/attr';
import { applic } from '@osee/shared/types/applicability';
import { UnitDropdownComponent } from '@osee/messaging/shared/dropdowns';

@Component({
	selector: 'osee-edit-type-dialog',
	templateUrl: './edit-type-dialog.component.html',
	styleUrls: ['./edit-type-dialog.component.sass'],
	standalone: true,
	imports: [
		NgIf,
		FormsModule,
		AsyncPipe,
		MatDialogModule,
		MatStepperModule,
		MatFormFieldModule,
		MatInputModule,
		MatSelectModule,
		MatOptionModule,
		A11yModule,
		MatButtonModule,
		MatOptionLoadingComponent,
		UniquePlatformTypeNameDirective,
		UniquePlatformTypeAttributesDirective,
		EditEnumSetFieldComponent,
		ApplicabilitySelectorComponent,
		UnitDropdownComponent,
	],
})
export class EditTypeDialogComponent implements OnDestroy {
	platform_type: string = '';
	logicalTypes = this.typesService.logicalTypes.pipe(
		switchMap((logicalTypes) =>
			from(logicalTypes).pipe(
				map((type) => {
					let name = type.name.replace('nsigned ', '');
					if (name !== type.name) {
						name =
							name.charAt(0) +
							name.charAt(1).toUpperCase() +
							name.slice(2);
					}
					type.name = name;
					return type;
				})
			)
		),
		reduce((acc, curr) => [...acc, curr], [] as logicalType[])
	);
	enumSet: {
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	} = {
		createArtifacts: [],
		modifyArtifacts: [],
		deleteRelations: [],
	};

	private enumSetManifest = new BehaviorSubject<{
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}>({ createArtifacts: [], modifyArtifacts: [], deleteRelations: [] });
	private pTypeManifest = new BehaviorSubject<{
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}>({ createArtifacts: [], modifyArtifacts: [], deleteRelations: [] });
	protected manifest = combineLatest([
		this.enumSetManifest,
		this.pTypeManifest,
	]).pipe(
		map(([enumSet, pType]) => {
			return {
				createArtifacts: [
					...enumSet.createArtifacts,
					...pType.createArtifacts,
				],
				modifyArtifacts: [
					...enumSet.modifyArtifacts,
					...pType.modifyArtifacts,
				],
				deleteRelations: [
					...enumSet.deleteRelations,
					...pType.deleteRelations,
				],
			};
		}),
		map((changeManifest) => {
			return {
				manifest: changeManifest,
				mode: this.data.mode,
			};
		})
	);
	parentMatcher = new ParentErrorStateMatcher();

	enumUnique = new Subject<string>();
	private _logicalType = new Subject<string>();

	private _done = new Subject();
	private _fieldInfo = combineLatest([
		this._logicalType,
		this.logicalTypes,
	]).pipe(
		filter(([selectedType, types]) =>
			types.map((t) => t.name).includes(selectedType)
		),
		switchMap(([selectedType, types]) =>
			of(types).pipe(
				concatMap((types) => from(types)),
				filter((type) => type.name === selectedType),
				take(1)
			)
		),
		switchMap((type) =>
			this.typesService.getLogicalTypeFormDetail(type.id)
		),
		map((result) => result.fields),
		concatMap((fields) => from(fields)),
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntil(this._done)
	);

	bitSizeRequired = this._fieldInfo.pipe(
		filter(
			(v) =>
				v.attributeTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE
		),
		map((v) => v.required)
	);

	bitResolutionRequired = this._fieldInfo.pipe(
		filter(
			(v) =>
				v.attributeTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSRESOLUTION
		),
		map((v) => v.required)
	);

	compRateRequired = this._fieldInfo.pipe(
		filter(
			(v) =>
				v.attributeTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPECOMPRATE
		),
		map((v) => v.required)
	);

	analogAccRequired = this._fieldInfo.pipe(
		filter(
			(v) =>
				v.attributeTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEANALOGACCURACY
		),
		map((v) => v.required)
	);

	unitsRequired = this._fieldInfo.pipe(
		filter(
			(v) =>
				v.attributeTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS
		),
		map((v) => v.required)
	);

	descriptionRequired = this._fieldInfo.pipe(
		filter((v) => v.attributeTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION),
		map((v) => v.required)
	);

	minValRequired = this._fieldInfo.pipe(
		filter(
			(v) =>
				v.attributeTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMINVAL
		),
		map((v) => v.required)
	);

	maxValRequired = this._fieldInfo.pipe(
		filter(
			(v) =>
				v.attributeTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMAXVAL
		),
		map((v) => v.required)
	);

	msbValRequired = this._fieldInfo.pipe(
		filter(
			(v) =>
				v.attributeTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMSBVAL
		),
		map((v) => v.required)
	);

	defaultValRequired = this._fieldInfo.pipe(
		filter(
			(v) => v.attributeTypeId === ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL
		),
		map((v) => v.required)
	);
	reference: Partial<PlatformType> = new PlatformTypeSentinel();

	constructor(
		public dialogRef: MatDialogRef<EditTypeDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: editPlatformTypeDialogData,
		private typesService: TypesService
	) {
		this.platform_type = this.data.type.name;
		this.reference = { ...this.data.type };
		if (this.data.mode === 0) {
			this.pTypeManifest.next({
				createArtifacts: [],
				modifyArtifacts: [
					{
						id: this.data.type.id || '-1',
						setAttributes: [],
						applicabilityId: this.data.type.applicability.id,
					},
				],
				deleteRelations: [],
			});
		} else {
			this.pTypeManifest.next({
				createArtifacts: [
					{
						typeId: ARTIFACTTYPEIDENUM.PLATFORMTYPE,
						applicabilityId: this.data.type.applicability.id,
						name: this.data.type.name,
						key: '3e1a2d30-f7db-43d2-af9d-d423115cbb8', //magic UUID to reference platform type
						attributes: [
							{
								typeId: ATTRIBUTETYPEIDENUM.DESCRIPTION,
								value: this.data.type.description,
							},
							{
								typeId: ATTRIBUTETYPEIDENUM.LOGICALTYPE,
								value: this.data.type.interfaceLogicalType,
							},
							{
								typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMINVAL,
								value: this.data.type
									.interfacePlatformTypeMinval,
							},
							{
								typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMAXVAL,
								value: this.data.type
									.interfacePlatformTypeMaxval,
							},
							{
								typeId: ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL,
								value: this.data.type.interfaceDefaultValue,
							},
							{
								typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMSBVAL,
								value: this.data.type
									.interfacePlatformTypeMsbValue,
							},
							{
								typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE,
								value: this.data.type
									.interfacePlatformTypeBitSize,
							},
							{
								typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSRESOLUTION,
								value: this.data.type
									.interfacePlatformTypeBitsResolution,
							},
							{
								typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPECOMPRATE,
								value: this.data.type
									.interfacePlatformTypeCompRate,
							},
							{
								typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEANALOGACCURACY,
								value: this.data.type
									.interfacePlatformTypeAnalogAccuracy,
							},
							{
								typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS,
								value: this.data.type
									.interfacePlatformTypeUnits,
							},
							{
								typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPE2SCOMPLEMENT,
								value: this.data.type
									.interfacePlatformType2sComplement,
							},
							{
								typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEVALIDRANGEDESCRIPTION,
								value: this.data.type
									.interfacePlatformTypeValidRangeDescription,
							},
						],
					},
				],
				modifyArtifacts: [],
				deleteRelations: [],
			});
			delete this.reference.id;
		}
	}

	ngOnDestroy(): void {
		this._done.next('');
	}

	/**
	 * Calculates MSB value based on Bit Resolution, Byte Size and whether or not the type is signed/unsigned
	 * @returns @type {string} MSB Value
	 */
	getMSBValue() {
		if (
			this.data.type.interfacePlatformTypeBitsResolution === '' ||
			this.data.type.interfacePlatformTypeBitSize === ''
		) {
			return '';
		}
		return (
			Number(this.data.type.interfacePlatformTypeBitsResolution) *
			(2 ^
				((Number(this.data.type.interfacePlatformTypeBitSize) * 8 -
					Number(this.data.type.interfacePlatformType2sComplement)) /
					2))
		).toString();
	}

	/**
	 * Calculates Resolution based on MSB value, Byte Size and whether or not the type is signed/unsigned
	 * @returns @type {string} Resolution
	 */
	getResolution() {
		if (
			this.data.type.interfacePlatformTypeMsbValue === '' ||
			this.data.type.interfacePlatformTypeBitSize === ''
		) {
			return '';
		}
		return (
			(Number(this.data.type.interfacePlatformTypeMsbValue) * 2) /
			(2 ^
				(Number(this.data.type.interfacePlatformTypeBitSize) * 8 -
					Number(this.data.type.interfacePlatformType2sComplement)))
		).toString();
	}

	/**
	 * Returns the bit size which is 8 * byte size
	 */
	get byte_size() {
		return Number(this.data.type.interfacePlatformTypeBitSize) / 8;
	}

	/**
	 * Sets the byte size based on bit size /8
	 */
	set byte_size(value: number) {
		this.data.type.interfacePlatformTypeBitSize = String(value * 8);
	}

	/**
	 * Forcefully closes dialog without returning data
	 */
	onNoClick(): void {
		this.dialogRef.close();
	}

	compareLogicalTypes(o1: string, o2: string) {
		if (o1 === null || o2 === null) return false;
		let val1 = o1.replace('nsigned ', '');
		if (val1 !== o1) {
			val1 =
				val1.charAt(0) + val1.charAt(1).toUpperCase() + val1.slice(2);
		}
		let val2 = o2.replace('nsigned ', '');
		if (val2 !== o2) {
			val2 =
				val2.charAt(0) + val2.charAt(1).toUpperCase() + val2.slice(2);
		}
		return val1 === val2;
	}
	enumUpdate(value: {
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}) {
		if (value) {
			this.enumSet = value;
			this.enumSetManifest.next(value);
		}
	}
	get returnValue(): {
		mode: editPlatformTypeDialogDataMode;
		type: enumeratedPlatformType;
		enumeration: {
			createArtifacts: createArtifact[];
			modifyArtifacts: modifyArtifact[];
			deleteRelations: modifyRelation[];
		};
	} {
		return {
			mode: this.data.mode,
			type: {
				...this.data.type,
				interfaceLogicalType: 'enumeration',
				enumerationSet: {
					name: '',
					applicability: {
						id: '1',
						name: 'Base',
					},
					description: '',
				},
			},
			enumeration: this.enumSet,
		};
	}
	updateUnique(value: boolean) {
		this.enumUnique.next(value.toString());
	}

	updateLogicalType(value: string) {
		this._logicalType.next(value);
		this.updatePTypeManifest(ATTRIBUTETYPEIDENUM.LOGICALTYPE, value);
	}

	updatePTypeManifest(
		attributeType: PLATFORMTYPEATTRIBUTETYPEID,
		value: string
	) {
		const newValue = this.pTypeManifest.getValue();
		if (this.data.mode === 0) {
			if (
				newValue.modifyArtifacts[0].setAttributes
					?.map((v) => v.typeId)
					.includes(attributeType)
			) {
				//replace the existing value
				const index = newValue.modifyArtifacts[0].setAttributes
					?.map((v) => v.typeId)
					.indexOf(attributeType);
				if (index !== -1) {
					newValue.modifyArtifacts[0].setAttributes[index] = {
						typeId: attributeType,
						value: value,
					};
				}
			} else {
				newValue.modifyArtifacts[0].setAttributes?.push({
					typeId: attributeType,
					value: value,
				});
			}
		} else {
			if (attributeType === ATTRIBUTETYPEIDENUM.NAME) {
				newValue.createArtifacts[0].name = value;
			} else {
				if (
					newValue.createArtifacts[0].attributes
						?.map((v) => v.typeId)
						.includes(attributeType)
				) {
					//replace the existing value
					const index = newValue.createArtifacts[0].attributes
						?.map((v) => v.typeId)
						.indexOf(attributeType);
					if (index !== -1) {
						newValue.createArtifacts[0].attributes[index] = {
							typeId: attributeType,
							value: value,
						};
					}
				} else {
					newValue.createArtifacts[0].attributes?.push({
						typeId: attributeType,
						value: value,
					});
				}
			}
		}
		this.pTypeManifest.next(newValue);
	}
	modifyApplicability(applicability: applic) {
		const newValue = this.pTypeManifest.getValue();
		if (this.data.mode === 0) {
			newValue.modifyArtifacts[0].applicabilityId = applicability.id;
		} else {
			newValue.createArtifacts[0].applicabilityId = applicability.id;
		}
		this.pTypeManifest.next(newValue);
	}
}
