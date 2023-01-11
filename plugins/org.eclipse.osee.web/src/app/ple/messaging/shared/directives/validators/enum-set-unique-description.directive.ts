/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Directive, Input } from '@angular/core';
import {
	AbstractControl,
	AsyncValidator,
	NG_ASYNC_VALIDATORS,
	ValidationErrors,
} from '@angular/forms';
import { Observable, of, switchMap, take } from 'rxjs';
import { CurrentQueryService } from '../../services/ui/current-query.service';
import { enumerationSet } from '../../types/enum';
import { andDescriptionQuery, andQuery, MimQuery } from '../../types/MimQuery';

@Directive({
	selector: '[oseeEnumSetUniqueDescription]',
	standalone: true,
	providers: [
		{
			provide: NG_ASYNC_VALIDATORS,
			useExisting: EnumSetUniqueDescriptionDirective,
			multi: true,
		},
	],
})
export class EnumSetUniqueDescriptionDirective implements AsyncValidator {
	@Input('oseeEnumSetUniqueDescription')
	enumSet: enumerationSet = {
		name: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
		description: '',
	};
	constructor(private queryService: CurrentQueryService) {}
	validate(
		control: AbstractControl<any, any>
	): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
		const attrQuery = new andDescriptionQuery(this.enumSet.description);
		const query = new MimQuery('2455059983007225791', undefined, [
			attrQuery,
		]);
		return this.queryService
			.queryExact(query as MimQuery<enumerationSet>)
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
