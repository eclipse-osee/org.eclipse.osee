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
import { Directive, forwardRef, Input } from '@angular/core';
import {
	AbstractControl,
	AsyncValidator,
	NG_ASYNC_VALIDATORS,
	ValidationErrors,
} from '@angular/forms';
import { CurrentQueryService } from '@osee/messaging/shared/services';
import type { PlatformType } from '@osee/messaging/shared/types';
import {
	andQuery,
	MimQuery,
	PlatformTypeQuery,
} from '@osee/messaging/shared/query';
import { debounceTime, map, Observable, of, switchMap, take } from 'rxjs';
import { ATTRIBUTETYPEIDENUM } from '@osee/shared/types/constants';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';

@Directive({
	selector: '[oseeUniquePlatformTypeName]',
	standalone: true,
	providers: [
		{
			provide: NG_ASYNC_VALIDATORS,
			useExisting: forwardRef(() => UniquePlatformTypeNameDirective),
			multi: true,
		},
	],
})
export class UniquePlatformTypeNameDirective implements AsyncValidator {
	@Input() referencePlatform: PlatformType = new PlatformTypeSentinel();
	private __referencePlatform = new PlatformTypeSentinel();
	constructor(private queryService: CurrentQueryService) {}

	validate(
		control: AbstractControl<string, string>
	): Observable<ValidationErrors | null> {
		return of(control.value).pipe(
			debounceTime(500),
			switchMap((name) =>
				of(new andQuery(ATTRIBUTETYPEIDENUM.NAME, name)).pipe(
					map(
						(nameQuery) =>
							new PlatformTypeQuery(undefined, [nameQuery])
					),
					switchMap((query) =>
						this.queryService.queryExact(
							query as MimQuery<PlatformType>
						)
					),
					switchMap((results) =>
						results.length > 0 &&
						this.referencePlatform.id !==
							this.__referencePlatform.id &&
						!results
							.map((v) => v.id)
							.includes(this.referencePlatform.id || '-1')
							? of<ValidationErrors>({
									notUnique: { value: name },
							  })
							: of(null)
					)
				)
			),
			take(1)
		);
	}
}
