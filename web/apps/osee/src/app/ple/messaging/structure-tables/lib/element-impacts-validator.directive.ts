/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { Directive, effect, inject, input } from '@angular/core';
import {
	AbstractControl,
	AsyncValidator,
	NG_ASYNC_VALIDATORS,
	ValidationErrors,
} from '@angular/forms';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { Observable, map, of } from 'rxjs';

@Directive({
	selector: '[oseeElementImpactsValidator]',
	standalone: true,
	providers: [
		{
			provide: NG_ASYNC_VALIDATORS,
			useExisting: ElementImpactsValidatorDirective,
			multi: true,
		},
	],
})
export class ElementImpactsValidatorDirective implements AsyncValidator {
	_control!: AbstractControl;
	artifactId = input<`${number}`>('-1');

	private structureService = inject(STRUCTURE_SERVICE_TOKEN);
	private _noopEffect = effect(() => {
		//TODO remove this effect once signals are fully integrated into forms
		this.artifactId();
		if (this._control) {
			this._control.updateValueAndValidity();
		}
	});
	validate(
		control: AbstractControl<never, never>
	): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
		this._control = control;
		if (this.artifactId() === '0' || this.artifactId() === '1') {
			return of<ValidationErrors>({ invalidArtifact: this.artifactId() });
		}
		if (!control.dirty) {
			return of(null);
		}
		return this.structureService
			.validateElement(this.artifactId())
			.pipe(
				map((v) =>
					v === false
						? { cancelledTransaction: this.artifactId() }
						: null
				)
			);
	}
}
