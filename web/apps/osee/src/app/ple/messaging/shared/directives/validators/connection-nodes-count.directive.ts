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
import { Directive, effect, forwardRef, input } from '@angular/core';
import {
	AbstractControl,
	NG_VALIDATORS,
	ValidationErrors,
	Validator,
} from '@angular/forms';
import { nodeData, transportType } from '@osee/messaging/shared/types';

@Directive({
	selector: '[oseeNodesCount]',
	standalone: true,
	providers: [
		{
			provide: NG_VALIDATORS,
			useExisting: forwardRef(() => NodesCountDirective),
			multi: true,
		},
	],
})
export class NodesCountDirective implements Validator {
	//TODO remove this variable once signals are fully integrated into forms
	_control!: AbstractControl;
	oseeNodesCount = input.required<transportType>();
	validationType = input<'connection' | 'publish' | 'subscribe'>(
		'connection'
	);

	private _noopEffect = effect(() => {
		//TODO remove this effect once signals are fully integrated into forms
		this.oseeNodesCount();
		this.validationType();
		if (this._control) {
			this._control.updateValueAndValidity();
		}
	});

	validate(control: AbstractControl<nodeData[]>): ValidationErrors | null {
		//TODO remove this variable once signals are fully integrated into forms
		this._control = control;
		const type = this.oseeNodesCount();
		const validationType = this.validationType();
		const min =
			validationType === 'connection'
				? Math.min(
						type.minimumPublisherMultiplicity.value,
						type.minimumSubscriberMultiplicity.value
					)
				: validationType === 'publish'
					? type.minimumPublisherMultiplicity.value
					: type.minimumSubscriberMultiplicity.value;
		if (!control.value) {
			return {
				min: {
					min: min,
					actual: 0,
				},
			};
		}
		const length = control.value.length;
		const max =
			validationType === 'connection'
				? type.maximumPublisherMultiplicity.value === 0 ||
					type.maximumSubscriberMultiplicity.value === 0
					? 0
					: type.maximumPublisherMultiplicity.value +
						type.maximumSubscriberMultiplicity.value
				: validationType === 'publish'
					? type.maximumPublisherMultiplicity.value
					: type.maximumSubscriberMultiplicity.value;

		//TODO all logic related to direct connection should be separate validator
		const directConnection = type.directConnection;
		if (
			(directConnection &&
				length !== 2 &&
				validationType === 'connection') ||
			(length < 2 && validationType === 'connection')
		) {
			return {
				min: {
					min: 2,
					actual: length,
				},
			};
		}
		if (min !== 0 && length < min) {
			return {
				min: {
					min: min,
					actual: length,
				},
			};
		} else if (max !== 0 && length > max) {
			return {
				max: {
					max: max,
					actual: length,
				},
			};
		}
		return null;
	}
}
