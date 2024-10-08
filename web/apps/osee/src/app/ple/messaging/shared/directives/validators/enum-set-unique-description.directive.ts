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
import { Directive, Input, inject } from '@angular/core';
import {
	AbstractControl,
	AsyncValidator,
	NG_ASYNC_VALIDATORS,
	ValidationErrors,
} from '@angular/forms';
import { CurrentQueryService } from '@osee/messaging/shared/services';
import { andDescriptionQuery, MimQuery } from '@osee/messaging/shared/query';
import type { enumerationSet } from '@osee/messaging/shared/types';
import { Observable, of, switchMap, take } from 'rxjs';

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
	private queryService = inject(CurrentQueryService);

	@Input('oseeEnumSetUniqueDescription')
	enumSet: enumerationSet = {
		name: {
			id: '-1',
			value: '',
			typeId: '1152921504606847088',
			gammaId: '-1',
		},
		applicability: {
			id: '1',
			name: 'Base',
		},
		description: {
			id: '-1',
			value: '',
			typeId: '1152921504606847090',
			gammaId: '-1',
		},
		enumerations: [],
		id: '-1',
		gammaId: '-1',
	};

	validate(
		_control: AbstractControl<never, never>
	): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
		const attrQuery = new andDescriptionQuery(
			this.enumSet.description.value
		);
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
