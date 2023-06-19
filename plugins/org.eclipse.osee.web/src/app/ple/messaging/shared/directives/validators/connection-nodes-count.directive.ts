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
import { Directive, forwardRef, Input } from '@angular/core';
import {
	AbstractControl,
	AsyncValidator,
	NG_ASYNC_VALIDATORS,
	ValidationErrors,
} from '@angular/forms';
import { nodeData, transportType } from '@osee/messaging/shared/types';
import { Observable, of } from 'rxjs';

@Directive({
	selector: '[oseeConnectionNodesCount]',
	standalone: true,
	providers: [
		{
			provide: NG_ASYNC_VALIDATORS,
			useExisting: forwardRef(() => ConnectionNodesCountDirective),
			multi: true,
		},
	],
})
export class ConnectionNodesCountDirective implements AsyncValidator {
	@Input() oseeConnectionNodesCount!: transportType;

	constructor() {}

	validate(
		control: AbstractControl<nodeData[]>
	): Observable<ValidationErrors | null> {
		const type = this.oseeConnectionNodesCount;
		const length = control.value.length;
		const min = Math.min(
			type.minimumPublisherMultiplicity,
			type.minimumSubscriberMultiplicity
		);
		const max =
			type.maximumPublisherMultiplicity === 0 ||
			type.maximumSubscriberMultiplicity === 0
				? 0
				: type.maximumPublisherMultiplicity +
				  type.maximumSubscriberMultiplicity;
		if (!control.value) {
			return of<ValidationErrors>({
				min: {
					min: min,
					actual: 0,
				},
			});
		}
		if (min !== 0 && length < min) {
			return of<ValidationErrors>({
				min: {
					min: min,
					actual: length,
				},
			});
		} else if (max !== 0 && length > max) {
			return of<ValidationErrors>({
				max: {
					max: max,
					actual: length,
				},
			});
		}
		return of(null);
	}
}
