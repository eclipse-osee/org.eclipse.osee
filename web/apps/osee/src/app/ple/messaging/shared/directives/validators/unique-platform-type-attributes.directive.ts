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
import { Directive, Input, Optional, inject } from '@angular/core';
import {
	AbstractControl,
	AsyncValidator,
	NG_ASYNC_VALIDATORS,
	ValidationErrors,
} from '@angular/forms';
import { CurrentQueryService } from '@osee/messaging/shared/services';
import type {
	logicalTypeFormDetail,
	PlatformType,
	PlatformTypeAttr,
} from '@osee/messaging/shared/types';
import {
	andQuery,
	MimQuery,
	PlatformTypeQuery,
} from '@osee/messaging/shared/query';
import { Observable, of, switchMap, take } from 'rxjs';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

@Directive({
	selector: '[oseeUniquePlatformTypeAttributes]',
	standalone: true,
	providers: [
		{
			provide: NG_ASYNC_VALIDATORS,
			useExisting: UniquePlatformTypeAttributesDirective,
			multi: true,
		},
	],
	exportAs: 'oseeUniquePlatformTypeAttributes',
})
export class UniquePlatformTypeAttributesDirective implements AsyncValidator {
	private queryService = inject(CurrentQueryService);

	@Optional()
	@Input('oseeUniquePlatformTypeAttributes')
	inputField?: logicalTypeFormDetail<keyof PlatformTypeAttr> = {
		id: '-1',
		name: '',
		idString: '',
		idIntValue: 0,
		fields: [],
	};
	@Input() referencePlatform: PlatformType | Partial<PlatformType> =
		new PlatformTypeSentinel();
	private __referencePlatform = new PlatformTypeSentinel();

	validate(
		control: AbstractControl<
			{
				byteSize: number;
				interfaceDefaultValue: string;
				interfaceDescription: string;
				interfaceLogicalType: string;
				interfacePlatformTypeAnalogAccuracy: string;
				interfacePlatformTypeBitSize: string;
				interfacePlatformTypeBitsResolution: string;
				interfacePlatformTypeCompRate: string;
				interfacePlatformTypeMaxval: string;
				interfacePlatformTypeMinval: string;
				interfacePlatformTypeMsbValue: string;
				interfacePlatformTypeUnits: string;
			},
			never
		>
	): Observable<ValidationErrors | null> {
		const nonEditableFields =
			this.inputField?.fields?.filter((field) => !field.editable) || [];
		const queries: andQuery[] = [];
		if (
			control.value.interfaceDescription !== '' &&
			control.value.interfaceDescription !== undefined
		)
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.DESCRIPTION,
					control.value.interfaceDescription
				)
			);
		if (
			control.value.interfaceLogicalType !== '' &&
			control.value.interfaceLogicalType !== undefined
		)
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.LOGICALTYPE,
					control.value.interfaceLogicalType
				)
			);
		if (
			control.value.interfacePlatformTypeAnalogAccuracy !== '' &&
			control.value.interfacePlatformTypeAnalogAccuracy
		)
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEANALOGACCURACY,
					control.value.interfacePlatformTypeAnalogAccuracy
				)
			);
		if (
			control.value.interfacePlatformTypeBitSize !== '' &&
			control.value.interfacePlatformTypeBitSize !== undefined
		)
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE,
					control.value.interfacePlatformTypeBitSize
				)
			);
		if (
			control.value.interfacePlatformTypeBitsResolution !== '' &&
			control.value.interfacePlatformTypeBitsResolution !== undefined
		)
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSRESOLUTION,
					control.value.interfacePlatformTypeBitsResolution
				)
			);
		if (
			control.value.interfacePlatformTypeCompRate !== '' &&
			control.value.interfacePlatformTypeCompRate !== undefined
		)
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPECOMPRATE,
					control.value.interfacePlatformTypeCompRate
				)
			);
		if (
			control.value.interfaceDefaultValue !== '' &&
			control.value.interfaceDefaultValue !== undefined
		)
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL,
					control.value.interfaceDefaultValue
				)
			);
		if (
			control.value.interfacePlatformTypeMaxval !== '' &&
			control.value.interfacePlatformTypeMaxval !== undefined
		)
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMAXVAL,
					control.value.interfacePlatformTypeMaxval
				)
			);
		if (
			control.value.interfacePlatformTypeMinval !== '' &&
			control.value.interfacePlatformTypeMinval !== undefined
		)
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMINVAL,
					control.value.interfacePlatformTypeMinval
				)
			);
		if (
			control.value.interfacePlatformTypeMsbValue !== '' &&
			control.value.interfacePlatformTypeMsbValue !== undefined
		)
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMSBVAL,
					control.value.interfacePlatformTypeMsbValue
				)
			);
		if (
			control.value.interfacePlatformTypeUnits !== '' &&
			control.value.interfacePlatformTypeUnits !== undefined
		)
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS,
					control.value.interfacePlatformTypeUnits
				)
			);
		nonEditableFields.forEach((field) => {
			queries.push(
				//@ts-expect-error field doesn't narrow properly
				new andQuery(field.attributeTypeId, field.defaultValue)
			);
		});
		if (
			this.inputField?.name !== '' &&
			this.inputField?.name !== undefined
		) {
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.LOGICALTYPE,
					this.inputField.name
				)
			);
		}
		const query = new PlatformTypeQuery(undefined, queries);
		return this.queryService
			.queryExact(query as MimQuery<PlatformType>)
			.pipe(
				take(1),
				switchMap((results) =>
					results.length > 0 &&
					this.referencePlatform.id !== this.__referencePlatform.id &&
					results
						.map((v) => v.id)
						.includes(this.referencePlatform?.id || '-1') &&
					results.length !== 1 &&
					this.referencePlatform.interfaceLogicalType?.value !==
						'enumeration'
						? of<ValidationErrors>({
								attributesNotUnique: { value: results.length },
							})
						: of(null)
				)
			);
	}
}
