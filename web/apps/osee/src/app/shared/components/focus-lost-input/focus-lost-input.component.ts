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
	output,
	signal,
} from '@angular/core';
import { outputFromObservable, toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatFormField,
	MatLabel,
	SubscriptSizing,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatTooltip } from '@angular/material/tooltip';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import { debounceTime, filter, sample } from 'rxjs';

let nextUniqueId = 0;
@Component({
	selector: 'osee-focus-lost-input',
	imports: [MatFormField, MatInput, MatTooltip, FormsModule, MatLabel],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: ` <mat-form-field
		[appearance]="appearance()"
		(focusin)="focus.set(true)"
		(focusout)="onFocusOut()"
		[subscriptSizing]="subscriptSizing()"
		class="tw-w-full tw-bg-inherit [&>.mdc-text-field--filled]:tw-bg-inherit">
		@if (label() !== '') {
			<mat-label>{{ label() }}</mat-label>
		}
		<input
			matInput
			[name]="'focus-lost-input-' + _componentId()"
			class="tw-text-inherit"
			[type]="type()"
			[ngModel]="value()"
			[ngModelOptions]="{ updateOn: 'blur' }"
			(ngModelChange)="value.set($event)"
			(input)="liveInput.emit($any($event.target).value)"
			[disabled]="disabled()"
			[maxlength]="maxlength()"
			[placeholder]="placeholder()"
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
	label = input('');
	appearance = input<'outline' | 'fill'>('outline');

	subscriptSizing = input<SubscriptSizing>('dynamic');
	focus = signal(false);
	type = input('text');
	tooltip = input<string>('');
	placeholder = input<string>('');
	maxlength = input<string | number | null>(null);

	protected onFocusOut() {
		this.focus.set(false);
	}

	private _focus$ = toObservable(this.focus);
	private _focus = this._focus$.pipe(
		debounceTime(500),
		filter((v) => !v)
	);
	private _value$ = toObservable(this.value);
	// Emit the latest value at the settled blur. The input binds
	// `updateOn: 'blur'`, so `value` only changes on blur -- it is already a
	// low-frequency, blur-gated stream. `sample(_focus)` picks the value present at
	// the settled-blur tick; there is nothing per-keystroke to coalesce, so no
	// debounce on the value is needed (the only debounce that matters is on `_focus`,
	// which absorbs focus flicker so a momentary blur/refocus is not a commit). An
	// earlier `debounceTime(500)` here was vestigial: it only added latency and a
	// second 500ms window racing the focus one, widening the stale-commit surface.
	//
	// This component is intentionally a "dumb" input: it reports commits
	// (valueChange) and keystrokes (liveInput) and does NOT know about conflicts or
	// staleness. A consumer that auto-saves owns deciding whether a given commit is
	// still valid (see the persisted attribute editor's edit-generation guard).
	private _value = this._value$.pipe(sample(this._focus));
	valueChange = outputFromObservable(this._value);

	/**
	 * Emits the raw input value on every keystroke (before blur). Unlike
	 * `valueChange` (which is debounced and only fires on focus loss), this lets
	 * consumers react to in-progress edits — e.g. to mark a field dirty for
	 * conflict detection while the user is still typing. Does not affect the
	 * blur-based commit/save behavior.
	 */
	liveInput = output<string>();
}
