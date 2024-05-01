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
import { TransportTypeUiService } from '@osee/messaging/shared/services';
import { ConnectionNode } from '@osee/messaging/shared/types';
import { Observable, of, switchMap } from 'rxjs';

@Directive({
	selector: '[oseeMessageNodesCount]',
	standalone: true,
	providers: [
		{
			provide: NG_ASYNC_VALIDATORS,
			useExisting: forwardRef(() => MessageNodesCountDirective),
			multi: true,
		},
	],
})
export class MessageNodesCountDirective implements AsyncValidator {
	@Input() oseeMessageNodesCount: boolean = true; // true = publisher, false = subscriber

	constructor(private transportTypeService: TransportTypeUiService) {}

	validate(
		control: AbstractControl<ConnectionNode[]>
	): Observable<ValidationErrors | null> {
		return this.transportTypeService.currentTransportType.pipe(
			switchMap((type) => {
				const min = this.oseeMessageNodesCount
					? type.minimumPublisherMultiplicity
					: type.minimumSubscriberMultiplicity;
				const max = this.oseeMessageNodesCount
					? type.maximumPublisherMultiplicity
					: type.maximumSubscriberMultiplicity;
				if (!control.value) {
					return of<ValidationErrors>({
						min: {
							min: min,
							actual: 0,
						},
					});
				}
				if (min !== 0 && control.value.length < min) {
					return of<ValidationErrors>({
						min: {
							min: min,
							actual: control.value.length,
						},
					});
				} else if (max !== 0 && control.value.length > max) {
					return of<ValidationErrors>({
						max: {
							max: max,
							actual: control.value.length,
						},
					});
				}
				return of(null);
			})
		);
	}
}
