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
import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import {
	MatError,
	MatFormField,
	MatHint,
	MatLabel,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { MatStep, MatStepper, MatStepperNext } from '@angular/material/stepper';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import {
	UniquePlatformTypeAttributesDirective,
	UniquePlatformTypeNameDirective,
} from '@osee/messaging/shared/directives';
import { CrossReferenceDropdownComponent } from '@osee/messaging/shared/dropdowns';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { EditEnumSetFieldComponent } from '@osee/messaging/shared/forms';
import { TypesService } from '@osee/messaging/shared/services';
import type {
	PlatformType,
	editPlatformTypeDialogData,
	logicalType,
} from '@osee/messaging/shared/types';
import { UnitDropdownComponent } from '@osee/messaging/units/dropdown';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { ParentErrorStateMatcher } from '@osee/shared/matchers';
import { writableSlice } from '@osee/shared/utils';
import { Subject, combineLatest, from, of } from 'rxjs';
import {
	concatMap,
	filter,
	map,
	reduce,
	shareReplay,
	switchMap,
	take,
} from 'rxjs/operators';

@Component({
	selector: 'osee-edit-type-dialog',
	templateUrl: './edit-type-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		AsyncPipe,
		MatDialogTitle,
		MatStepper,
		MatStep,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatInput,
		MatHint,
		MatError,
		MatSelect,
		MatOption,
		MatDialogActions,
		MatButton,
		MatStepperNext,
		MatDialogClose,
		CdkTextareaAutosize,
		MatOptionLoadingComponent,
		UniquePlatformTypeNameDirective,
		UniquePlatformTypeAttributesDirective,
		EditEnumSetFieldComponent,
		ApplicabilityDropdownComponent,
		UnitDropdownComponent,
		CrossReferenceDropdownComponent,
	],
})
export class EditTypeDialogComponent {
	dialogRef = inject<MatDialogRef<EditTypeDialogComponent>>(MatDialogRef);
	data = inject(MAT_DIALOG_DATA);
	private typesService = inject(TypesService);

	protected platformType = signal(
		inject<editPlatformTypeDialogData>(MAT_DIALOG_DATA).type
	);

	private _nameAttr = writableSlice(this.platformType, 'name');
	protected name = writableSlice(this._nameAttr, 'value');
	private _descriptionAttr = writableSlice(this.platformType, 'description');
	protected description = writableSlice(this._descriptionAttr, 'value');
	private _logicalTypeAttr = writableSlice(
		this.platformType,
		'interfaceLogicalType'
	);
	protected logicalType = writableSlice(this._logicalTypeAttr, 'value');
	private _complementAttr = writableSlice(
		this.platformType,
		'interfacePlatformType2sComplement'
	);
	protected complement = writableSlice(this._complementAttr, 'value');
	private _accuracyAttr = writableSlice(
		this.platformType,
		'interfacePlatformTypeAnalogAccuracy'
	);
	protected accuracy = writableSlice(this._accuracyAttr, 'value');
	private _resolutionAttr = writableSlice(
		this.platformType,
		'interfacePlatformTypeBitsResolution'
	);
	protected resolution = writableSlice(this._resolutionAttr, 'value');
	protected sizeAttr = writableSlice(
		this.platformType,
		'interfacePlatformTypeBitSize'
	);
	protected size = writableSlice(this.sizeAttr, 'value');
	private _compRateAttr = writableSlice(
		this.platformType,
		'interfacePlatformTypeCompRate'
	);
	protected compRate = writableSlice(this._compRateAttr, 'value');
	private _defaultValueAttr = writableSlice(
		this.platformType,
		'interfaceDefaultValue'
	);
	protected defaultValue = writableSlice(this._defaultValueAttr, 'value');
	private _maxValAttr = writableSlice(
		this.platformType,
		'interfacePlatformTypeMaxval'
	);
	protected maxVal = writableSlice(this._maxValAttr, 'value');
	private _minValAttr = writableSlice(
		this.platformType,
		'interfacePlatformTypeMinval'
	);
	protected minVal = writableSlice(this._minValAttr, 'value');
	private _msbValAttr = writableSlice(
		this.platformType,
		'interfacePlatformTypeMsbValue'
	);
	protected msbVal = writableSlice(this._msbValAttr, 'value');
	protected _unitsAttr = writableSlice(
		this.platformType,
		'interfacePlatformTypeUnits'
	);
	protected units = writableSlice(this._unitsAttr, 'value');
	private _validRangeAttr = writableSlice(
		this.platformType,
		'interfacePlatformTypeValidRangeDescription'
	);
	protected validRange = writableSlice(this._validRangeAttr, 'value');

	protected applicability = writableSlice(this.platformType, 'applicability');
	private _applicabilityId = computed(() => this.applicability().id);
	protected mode = signal(
		inject<editPlatformTypeDialogData>(MAT_DIALOG_DATA).mode
	);
	private _id = computed(() => this.platformType().id);
	protected enumSet = writableSlice(this.platformType, 'enumSet');

	private _platformType = toObservable(this.name);
	platformTypeTitle = this._platformType.pipe(take(1));
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

	parentMatcher = new ParentErrorStateMatcher();

	enumUnique = new Subject<string>();
	private _logicalType = toObservable(this.logicalType);

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
		takeUntilDestroyed()
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

	constructor() {
		this.reference = structuredClone(this.data.type);
	}

	/**
	 * Calculates MSB value based on Bit Resolution, Byte Size and whether or not the type is signed/unsigned
	 * @returns @type {string} MSB Value
	 */
	protected msbValue = computed(() => {
		if (this.resolution() === '' || this.size() === '') {
			return '';
		}
		return (
			Number(this.resolution()) *
			(2 ^ ((Number(this.size()) * 8 - Number(this.complement())) / 2))
		).toString();
	});

	/**
	 * Calculates Resolution based on MSB value, Byte Size and whether or not the type is signed/unsigned
	 * @returns @type {string} Resolution
	 */
	protected computedResolution = computed(() => {
		if (this.msbValue() === '' || this.size() === '') {
			return '';
		}
		return (
			(Number(this.msbValue()) * 2) /
			(2 ^ (Number(this.size()) * 8 - Number(this.complement())))
		).toString();
	});

	/**
	 * Returns the bit size which is 8 * byte size
	 */
	get byte_size() {
		return Number(this.size()) / 8;
	}

	/**
	 * Sets the byte size based on bit size /8
	 */
	set byte_size(value: number) {
		this.size.set(String(value * 8));
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
	updateUnique(value: boolean) {
		this.enumUnique.next(value.toString());
	}
}
