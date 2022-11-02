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
import { Directive } from '@angular/core';
import {
	AbstractControl,
	AsyncValidator,
	NG_ASYNC_VALIDATORS,
	ValidationErrors,
} from '@angular/forms';
import { Observable, of, switchMap, take } from 'rxjs';
import { ATTRIBUTETYPEID } from '../../../../../types/constants/AttributeTypeId.enum';
import { CurrentQueryService } from '../../services/ui/current-query.service';
import { andQuery, MimQuery, PlatformTypeQuery } from '../../types/MimQuery';
import { PlatformType } from '../../types/platformType';

@Directive({
	selector: '[oseeUniquePlatformTypeAttributes]',
	providers: [
		{
			provide: NG_ASYNC_VALIDATORS,
			useExisting: UniquePlatformTypeAttributesDirective,
			multi: true,
		},
	],
})
export class UniquePlatformTypeAttributesDirective implements AsyncValidator {
	constructor(private queryService: CurrentQueryService) {}
	validate(
		control: AbstractControl<PlatformType, any>
	): Observable<ValidationErrors | null> {
		const queries: andQuery[] = [];
		if (control.value.description !== '')
			queries.push(
				new andQuery(
					ATTRIBUTETYPEID.DESCRIPTION,
					control.value.description
				)
			);
		if (control.value.interfaceLogicalType !== '')
			queries.push(
				new andQuery(
					ATTRIBUTETYPEID.LOGICALTYPE,
					control.value.interfaceLogicalType
				)
			);
		if (control.value.interfacePlatformTypeAnalogAccuracy !== '')
			queries.push(
				new andQuery(
					ATTRIBUTETYPEID.INTERFACEPLATFORMTYPEANALOGACCURACY,
					control.value.interfacePlatformTypeAnalogAccuracy
				)
			);
		if (control.value.interfacePlatformTypeBitSize !== '')
			queries.push(
				new andQuery(
					ATTRIBUTETYPEID.INTERFACEPLATFORMTYPEBITSIZE,
					control.value.interfacePlatformTypeBitSize
				)
			);
		if (control.value.interfacePlatformTypeBitsResolution !== '')
			queries.push(
				new andQuery(
					ATTRIBUTETYPEID.INTERFACEPLATFORMTYPEBITSRESOLUTION,
					control.value.interfacePlatformTypeBitsResolution
				)
			);
		if (control.value.interfacePlatformTypeCompRate !== '')
			queries.push(
				new andQuery(
					ATTRIBUTETYPEID.INTERFACEPLATFORMTYPECOMPRATE,
					control.value.interfacePlatformTypeCompRate
				)
			);
		if (control.value.interfacePlatformTypeDefaultValue !== '')
			queries.push(
				new andQuery(
					ATTRIBUTETYPEID.INTERFACEPLATFORMTYPEDEFAULTVAL,
					control.value.interfacePlatformTypeDefaultValue
				)
			);
		if (control.value.interfacePlatformTypeMaxval !== '')
			queries.push(
				new andQuery(
					ATTRIBUTETYPEID.INTERFACEPLATFORMTYPEMAXVAL,
					control.value.interfacePlatformTypeMaxval
				)
			);
		if (control.value.interfacePlatformTypeMinval !== '')
			queries.push(
				new andQuery(
					ATTRIBUTETYPEID.INTERFACEPLATFORMTYPEMINVAL,
					control.value.interfacePlatformTypeMinval
				)
			);
		if (control.value.interfacePlatformTypeMsbValue !== '')
			queries.push(
				new andQuery(
					ATTRIBUTETYPEID.INTERFACEPLATFORMTYPEMSBVAL,
					control.value.interfacePlatformTypeMsbValue
				)
			);
		if (control.value.interfacePlatformTypeUnits !== '')
			queries.push(
				new andQuery(
					ATTRIBUTETYPEID.INTERFACEPLATFORMTYPEUNITS,
					control.value.interfacePlatformTypeUnits
				)
			);
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
