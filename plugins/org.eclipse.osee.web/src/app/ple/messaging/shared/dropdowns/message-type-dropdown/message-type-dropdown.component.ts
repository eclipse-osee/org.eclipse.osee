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
import { AsyncPipe, CommonModule, NgIf } from '@angular/common';
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
	MatOptionModule,
	ErrorStateMatcher,
	ShowOnDirtyErrorStateMatcher,
} from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { CurrentMessageTypesService } from '@osee/messaging/shared/services';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { NamedId } from '@osee/shared/types';
import {
	BehaviorSubject,
	ReplaySubject,
	Subject,
	skip,
	debounceTime,
	distinctUntilChanged,
	switchMap,
	of,
} from 'rxjs';

@Component({
	selector: 'osee-message-type-dropdown',
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		AsyncPipe,
		NgIf,
		FormsModule,
		MatInputModule,
		MatOptionModule,
		MatFormFieldModule,
		MatAutocompleteModule,
		MatIconModule,
		MatButtonModule,
		MatOptionLoadingComponent,
	],
	templateUrl: './message-type-dropdown.component.html',
	styleUrls: ['./message-type-dropdown.component.sass'],
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
export class MessageTypeDropdownComponent implements OnChanges {
	private _currentMessageTypesService = inject(CurrentMessageTypesService);

	private _typeAhead = new BehaviorSubject<string>('');
	private _openAutoComplete = new ReplaySubject<void>();

	private _isOpen = new BehaviorSubject<boolean>(false);

	@Input() required: boolean = false;
	@Input() disabled: boolean = false;

	@Input() hintHidden: boolean = false;
	@Input() messageType: string = '';

	private _messageTypeChange = new Subject<string>();
	@Output() messageTypeChange = this._messageTypeChange.pipe(skip(1));

	@Input() errorMatcher: ErrorStateMatcher =
		new ShowOnDirtyErrorStateMatcher();

	protected _size = this._currentMessageTypesService.currentPageSize;

	protected _messageTypes = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeAhead.pipe(
				distinctUntilChanged(),
				debounceTime(500),
				switchMap((filter) =>
					of((pageNum: string | number) =>
						this._currentMessageTypesService.getFilteredPaginatedMessageTypes(
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
					this._currentMessageTypesService.getFilteredCount(filter)
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
		} else {
			this._typeAhead.next(value.name);
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
		this._messageTypeChange.next(value);
		this.updateTypeAhead(value);
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (
			changes.messageType !== undefined &&
			changes.messageType.previousValue !==
				changes.messageType.currentValue &&
			changes.messageType.currentValue !== undefined
		) {
			this.updateValue(changes.messageType.currentValue);
		}
	}
	get isOpen() {
		return this._isOpen;
	}
	clear() {
		this.updateTypeAhead('');
	}
}
