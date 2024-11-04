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
import {
	ChangeDetectionStrategy,
	Component,
	input,
	model,
	signal,
} from '@angular/core';
import { outputFromObservable, toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatFormField, SubscriptSizing } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatTooltip } from '@angular/material/tooltip';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import { debounceTime, filter, sample } from 'rxjs';

let nextUniqueId = 0;
@Component({
	selector: 'osee-focus-lost-input',
	standalone: true,
	imports: [MatFormField, MatInput, MatTooltip, FormsModule],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: ` <mat-form-field
		(focusin)="focus.set(true)"
		(focusout)="focus.set(false)"
		[subscriptSizing]="subscriptSizing()"
		class="tw-w-full tw-bg-inherit tw-px-2 tw-text-inherit [&>.mat-mdc-form-field-input-control]:tw-text-inherit [&>.mdc-text-field--filled]:tw-bg-inherit">
		<input
			matInput
			[name]="'focus-lost-input-' + _componentId()"
			class="tw-text-inherit"
			[type]="type()"
			[ngModel]="value()"
			[ngModelOptions]="{ updateOn: 'blur' }"
			(ngModelChange)="value.set($event)"
			[disabled]="disabled()"
			[maxlength]="maxlength()"
			[matTooltip]="tooltip()" />
		<ng-content />
	</mat-form-field>`,
	//TODO make this a real CVA?
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class FocusLostInputComponent<T> {
	protected _componentId = signal(`${nextUniqueId++}`);
	disabled = input(false);
	value = model.required<T>();

	subscriptSizing = input<SubscriptSizing>('dynamic');
	focus = signal(false);
	type = input('text');
	tooltip = input<string>('');
	maxlength = input<string | number | null>(null);
	private _focus$ = toObservable(this.focus);
	private _focus = this._focus$.pipe(
		debounceTime(500),
		filter((v) => !v)
	);
	private _value$ = toObservable(this.value);
	private _value = this._value$.pipe(debounceTime(500), sample(this._focus));
	valueChange = outputFromObservable(this._value);
}
