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
	animate,
	state,
	style,
	transition,
	trigger,
} from '@angular/animations';
import { AsyncPipe } from '@angular/common';
import {
	Component,
	Optional,
	computed,
	inject,
	model,
	signal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import { MatIcon } from '@angular/material/icon';
import {
	MatFormField,
	MatHint,
	MatInput,
	MatLabel,
	MatSuffix,
} from '@angular/material/input';
import { ActionService } from '@osee/shared/services';
import { atsLastMod } from '@osee/shared/types/configuration-management';
import {
	Observable,
	debounceTime,
	distinctUntilChanged,
	of,
	scan,
	switchMap,
} from 'rxjs';
import { MatOptionLoadingComponent } from '../mat-option-loading/mat-option-loading/mat-option-loading.component';
function controlContainerFactory(controlContainer?: ControlContainer) {
	return controlContainer;
}
let nextUniqueId = 0;
@Component({
	selector: 'osee-latest-action-drop-down',
	standalone: true,
	imports: [
		MatOptionLoadingComponent,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatInput,
		MatIcon,
		MatOption,
		MatFormField,
		MatLabel,
		MatSuffix,
		MatHint,
		FormsModule,
		AsyncPipe,
	],
	animations: [
		trigger('dropdownOpen', [
			state(
				'open',
				style({
					opacity: 0,
				})
			),
			state(
				'closed',
				style({
					opacity: 1,
				})
			),
			transition('open=>closed', [animate('0.5s')]),
			transition('closed=>open', [animate('0.5s 0.25s')]),
		]),
	],
	templateUrl: './latest-action-drop-down.component.html',
	viewProviders: [
		{
			provide: ControlContainer,
			useFactory: controlContainerFactory,
			deps: [[new Optional(), NgForm]],
		},
	],
})
export class LatestActionDropDownComponent {
	protected _componentId = signal(`${nextUniqueId++}`);
	parentAction = model<atsLastMod>({
		atsId: '',
		id: '',
		lastMod: 0,
		siblings: [],
		opened: 0,
		closed: 0,
		name: '',
	});
	parentActionObs = toObservable(this.parentAction);
	filterSignal = signal('');

	filterObs = toObservable(this.filterSignal);
	private _actionService = inject(ActionService);
	protected _openAutoCompleteSignal = signal(false);
	protected _openAutoCompleteObs = toObservable(this._openAutoCompleteSignal);
	protected _openAutoCompleteCountObs = this._openAutoCompleteObs.pipe(
		scan((acc, curr) => acc + 1, 0)
	);
	protected _openAutoCompleteCountSignal = toSignal(
		this._openAutoCompleteCountObs,
		{ initialValue: 0 }
	);
	protected _isOpenSignal = computed(
		() => this._openAutoCompleteCountSignal() % 2 === 1
	);
	protected _size = signal(10).asReadonly();

	protected _innerActions = this.filterObs.pipe(
		switchMap((f) =>
			of((pageNum: string | number) =>
				this._actionService.getLastModifiedAtsAction(
					this._size(),
					pageNum,
					f
				)
			)
		)
	);
	private _innerCount = this.filterObs.pipe(
		switchMap((filter) =>
			this._actionService.getLastModifiedCountAtsAction(filter)
		)
	);

	getTypeAheadObservable<T>(value: Observable<T>) {
		return this._openAutoCompleteObs.pipe(
			debounceTime(500),
			distinctUntilChanged(),
			switchMap((_) => value)
		);
	}

	protected _count = this.getTypeAheadObservable(this._innerCount);

	protected _actions = this.getTypeAheadObservable(this._innerActions);

	displayFn(teamwf: atsLastMod): string {
		return teamwf && teamwf.name ? teamwf.name : '';
	}

	updateTypeAhead(value: string | atsLastMod) {
		if (typeof value === 'string') {
			this.filterSignal.set(value);
			return;
		}
		this.filterSignal.set(value.name);
		if (this._isOpenSignal()) {
			this._openAutoCompleteSignal.update((v) => !v);
		}
	}
	clear() {
		const clearValue = {
			atsId: '',
			id: '',
			lastMod: 0,
			siblings: [],
			opened: 0,
			closed: 0,
			name: '',
		};
		this.parentAction.set(clearValue);
		this.updateTypeAhead(clearValue);
	}

	updateOpenState() {
		this._openAutoCompleteSignal.update((v) => !v);
	}
}
