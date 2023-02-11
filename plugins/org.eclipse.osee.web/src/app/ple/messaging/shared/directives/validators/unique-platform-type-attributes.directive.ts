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
import { Directive, Input, Optional } from '@angular/core';
import {
	AbstractControl,
	AsyncValidator,
	NG_ASYNC_VALIDATORS,
	ValidationErrors,
} from '@angular/forms';
import { CurrentQueryService } from '@osee/messaging/shared/services';
import {
	logicalTypeFormDetail,
	PlatformType,
	andQuery,
	PlatformTypeQuery,
	MimQuery,
} from '@osee/messaging/shared/types';
import { Observable, of, switchMap, take } from 'rxjs';
import { ATTRIBUTETYPEIDENUM } from '@osee/shared/types/constants';

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
	@Optional()
	@Input('oseeUniquePlatformTypeAttributes')
	inputField?: logicalTypeFormDetail = {
		id: '',
		name: '',
		idString: '',
		idIntValue: 0,
		fields: [],
	};
	constructor(private queryService: CurrentQueryService) {}
	validate(
		control: AbstractControl<PlatformType, any>
	): Observable<ValidationErrors | null> {
		const nonEditableFields =
			this.inputField?.fields?.filter((field) => !field.editable) || [];
		const queries: andQuery[] = [];
		if (
			control.value.description !== '' &&
			control.value.description !== undefined
		)
			queries.push(
				new andQuery(
					ATTRIBUTETYPEIDENUM.DESCRIPTION,
					control.value.description
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
					results.length > 0
						? of<ValidationErrors>({
								attributesNotUnique: { value: results.length },
						  })
						: of(null)
				)
			);
	}
}
