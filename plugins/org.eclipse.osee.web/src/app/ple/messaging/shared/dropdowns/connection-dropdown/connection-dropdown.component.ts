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
	Input,
	OnChanges,
	Output,
	SimpleChanges,
	inject,
} from '@angular/core';
import { AsyncPipe, NgIf } from '@angular/common';
import {
	BehaviorSubject,
	ReplaySubject,
	Subject,
	debounceTime,
	distinctUntilChanged,
	of,
	switchMap,
} from 'rxjs';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import {
	ErrorStateMatcher,
	MatOptionModule,
	ShowOnDirtyErrorStateMatcher,
} from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { connection, connectionSentinel } from '@osee/messaging/shared/types';
import { CurrentConnectionsService } from 'src/app/ple/messaging/shared/services/ui/current-connections.service';
import {
	animate,
	state,
	style,
	transition,
	trigger,
} from '@angular/animations';

@Component({
	selector: 'osee-connection-dropdown',
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
	templateUrl: './connection-dropdown.component.html',
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
})
export class ConnectionDropdownComponent implements OnChanges {
	@Input() required: boolean = false;
	@Input() disabled: boolean = false;
	@Input() showNoneOption: boolean = false;
	@Input() connection: connection = connectionSentinel;
	@Input() errorMatcher: ErrorStateMatcher =
		new ShowOnDirtyErrorStateMatcher();

	@Output() connectionChange = new Subject<connection>();

	private _currentConnectionsService = inject(CurrentConnectionsService);

	private _typeAhead = new BehaviorSubject<string>('');
	private _openAutoComplete = new ReplaySubject<void>();
	private _isOpen = new BehaviorSubject<boolean>(false);

	protected _size = this._currentConnectionsService.currentPageSize;

	noneOption = { ...connectionSentinel, name: 'None' };

	protected _connections = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeAhead.pipe(
				distinctUntilChanged(),
				debounceTime(500),
				switchMap((filter) =>
					of((pageNum: string | number) =>
						this._currentConnectionsService.getFilteredPaginatedConnections(
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
					this._currentConnectionsService.getFilteredCount(filter)
				)
			)
		)
	);

	updateTypeAhead(value: string) {
		this._typeAhead.next(value);
	}
	autoCompleteOpened() {
		this._openAutoComplete.next();
		this._isOpen.next(true);
	}
	close() {
		this._isOpen.next(false);
	}
	updateValue(value: connection) {
		this.connectionChange.next(value);
		this.updateTypeAhead(value.name);
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (
			changes.connection !== undefined &&
			changes.connection.previousValue !==
				changes.connection.currentValue &&
			changes.connection.currentValue !== undefined
		) {
			this.updateValue(changes.connection.currentValue);
		}
	}

	clear() {
		this.updateTypeAhead('');
	}

	get filter() {
		return this._typeAhead;
	}

	get isOpen() {
		return this._isOpen;
	}
}
