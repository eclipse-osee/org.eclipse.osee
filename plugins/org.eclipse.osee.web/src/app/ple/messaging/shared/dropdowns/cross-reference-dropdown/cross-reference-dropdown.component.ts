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
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	Input,
	OnChanges,
	Output,
	SimpleChanges,
} from '@angular/core';
import { AsyncPipe, CommonModule, NgFor, NgIf } from '@angular/common';
import {
	trigger,
	state,
	style,
	transition,
	animate,
} from '@angular/animations';
import { FormsModule, ControlContainer, NgForm } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import {
	ErrorStateMatcher,
	MatOptionModule,
	ShowOnDirtyErrorStateMatcher,
} from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { CrossReferenceService } from '@osee/messaging/shared/services';
import { NamedId } from '@osee/shared/types';
import {
	BehaviorSubject,
	ReplaySubject,
	Subject,
	debounceTime,
	distinctUntilChanged,
	switchMap,
	of,
	skip,
} from 'rxjs';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
	selector: 'osee-cross-reference-dropdown',
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		AsyncPipe,
		NgIf,
		NgFor,
		FormsModule,
		MatInputModule,
		MatOptionModule,
		MatFormFieldModule,
		MatAutocompleteModule,
		MatIconModule,
		MatButtonModule,
		MatOptionLoadingComponent,
		MatTooltipModule,
	],
	templateUrl: './cross-reference-dropdown.component.html',
	styleUrls: ['./cross-reference-dropdown.component.sass'],
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
	viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class CrossReferenceDropdownComponent implements OnChanges {
	private _currentCrossRefService = inject(CrossReferenceService);

	private _typeAhead = new BehaviorSubject<string>('');
	private _openAutoComplete = new ReplaySubject<void>();

	private _isOpen = new BehaviorSubject<boolean>(false);

	@Input() id: string = '';
	@Input() name: string = '';
	@Input() required: boolean = false;

	@Input() disabled: boolean = false;

	@Input() hintHidden: boolean = false;
	@Input() crossRef: string = '';

	_crossRefChange = new Subject<string>();
	@Output() crossRefChange = this._crossRefChange.pipe(
		debounceTime(100),
		skip(1)
	);

	@Input() errorMatcher: ErrorStateMatcher =
		new ShowOnDirtyErrorStateMatcher();

	@Input() allowOutsideValues: boolean = false;

	@Input() alternateObjectType: string = '';

	@Input() maximum: string = '';

	@Input() minimum: string = '';

	private _previousValue = '';

	protected _size = this._currentCrossRefService.currentPageSize;

	protected _crossRefs = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeAhead.pipe(
				distinctUntilChanged(),
				debounceTime(500),
				switchMap((filter) =>
					of((pageNum: string | number) =>
						this._currentCrossRefService.getFilteredPaginatedCrossRefs(
							pageNum,
							filter
						)
					)
				)
			)
		)
	);

	_count = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeAhead.pipe(
				switchMap((filter) =>
					this._currentCrossRefService.getFilteredCount(filter)
				)
			)
		)
	);

	get filter() {
		return this._typeAhead;
	}

	updateTypeAhead(value: string | NamedId) {
		if (typeof value === 'string') {
			this._typeAhead.next(value);
			if (this.allowOutsideValues && value !== this._previousValue) {
				this.updateValue(value);
			}
		} else {
			this._typeAhead.next(value.name);
			if (this.allowOutsideValues && value.name !== this._previousValue) {
				this.updateValue(value.name);
			}
		}
	}
	autoCompleteOpened() {
		this._openAutoComplete.next();
		this._isOpen.next(true);
	}
	close() {
		this._isOpen.next(false);
	}
	updateValue(value: string) {
		this._crossRefChange.next(value);
		if (this._typeAhead.getValue() !== value) {
			this.updateTypeAhead(value);
		}
		this._previousValue = value;
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (
			changes.crossRef !== undefined &&
			changes.crossRef.previousValue !== changes.crossRef.currentValue &&
			changes.crossRef.currentValue !== undefined
		) {
			this.updateValue(changes.crossRef.currentValue);
		}
	}
	get isOpen() {
		return this._isOpen;
	}
	clear() {
		this.updateTypeAhead('');
	}
}
