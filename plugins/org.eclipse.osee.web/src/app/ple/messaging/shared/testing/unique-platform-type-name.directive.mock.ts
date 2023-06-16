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
import { Directive, Input, Optional } from '@angular/core';
import {
	AbstractControl,
	AsyncValidator,
	NG_ASYNC_VALIDATORS,
	ValidationErrors,
} from '@angular/forms';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import type {
	logicalTypeFormDetail,
	PlatformType,
} from '@osee/messaging/shared/types';
import { Observable, of } from 'rxjs';

@Directive({
	selector: '[oseeUniquePlatformTypeName]',
	standalone: true,
	providers: [
		{
			provide: NG_ASYNC_VALIDATORS,
			useExisting: MockUniquePlatformTypeNameDirective,
			multi: true,
		},
	],
})
export class MockUniquePlatformTypeNameDirective implements AsyncValidator {
	@Optional()
	@Input('oseeUniquePlatformTypeAttributes')
	inputField?: logicalTypeFormDetail = {
		id: '',
		name: '',
		idString: '',
		idIntValue: 0,
		fields: [],
	};
	@Input() referencePlatform: PlatformType = new PlatformTypeSentinel();
	validate(
		control: AbstractControl<PlatformType, any>
	): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
		return of(null);
	}
}
