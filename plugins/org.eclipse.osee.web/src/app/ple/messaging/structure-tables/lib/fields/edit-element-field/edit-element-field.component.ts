/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import { A11yModule } from '@angular/cdk/a11y';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import {
	Component,
	OnDestroy,
	OnChanges,
	Input,
	Output,
	EventEmitter,
	Inject,
	SimpleChanges,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { RouterLink } from '@angular/router';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import {
	CurrentStructureService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import type { element, PlatformType } from '@osee/messaging/shared/types';
import {
	ApplicabilitySelectorComponent,
	MatOptionLoadingComponent,
} from '@osee/shared/components';
import {
	Subject,
	combineLatest,
	switchMap,
	of,
	BehaviorSubject,
	share,
	debounceTime,
	distinctUntilChanged,
	map,
	tap,
	scan,
	iif,
	ReplaySubject,
	filter,
	skip,
} from 'rxjs';
import { CdkDrag, CdkDragHandle } from '@angular/cdk/drag-drop';
import { applic } from '@osee/shared/types/applicability';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { UnitDropdownComponent } from '@osee/messaging/shared/dropdowns';

@Component({
	selector: 'osee-messaging-edit-element-field',
	templateUrl: './edit-element-field.component.html',
	styleUrls: ['./edit-element-field.component.sass'],
	standalone: true,
	imports: [
		MatFormFieldModule,
		FormsModule,
		NgIf,
		NgFor,
		MatSelectModule,
		MatOptionModule,
		MatInputModule,
		MatAutocompleteModule,
		MatOptionLoadingComponent,
		MatButtonModule,
		MatIconModule,
		AsyncPipe,
		A11yModule,
		RouterLink,
		ApplicabilitySelectorComponent,
		UnitDropdownComponent,
	],
})
export class EditElementFieldComponent<U extends keyof element>
	implements OnDestroy, OnChanges
{
	private _done = new Subject();
	availableTypes = this.structureService.types;
	@Input() structureId: string = '';
	@Input() elementId: string = '';
	@Input() header!: U;
	@Input() value!: element[U];
	@Input() elementStart: number = 0;
	@Input() elementEnd: number = 0;
	@Input() editingDisabled: boolean = false;

	@Input() platformType: PlatformType = new PlatformTypeSentinel();
	@Output() contextMenu = new EventEmitter<MouseEvent>();

	private _value: Subject<element[U]> = new Subject();
	private _immediateValue: Subject<element[U]> = new Subject();
	private _units: Subject<string> = new Subject();
	paginationSize = 10;
	_element: Partial<element> = {
		id: this.elementId,
	};
	_location = combineLatest([
		this.structureService.branchType,
		this.structureService.BranchId,
	]).pipe(switchMap(([type, id]) => of({ type: type, id: id })));
	/**
	 * Type ahead value when editing which platform type is set for the given element
	 */
	private _typeValue: BehaviorSubject<string> = new BehaviorSubject('');
	private _sendValue = this._value.pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		map((x) => (this._element[this.header] = x)),
		tap(() => {
			this._element.id = this.elementId;
		})
	);
	private _updateUnits = this._units.pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		skip(1), //note: we might want to move the skip above the debounceTime to make things a bit faster in the future
		switchMap((unit) =>
			this.structureService.updatePlatformTypeValue({
				id: this.platformType.id,
				interfacePlatformTypeUnits: unit,
			})
		)
	);
	private _immediateUpdateValue = this._immediateValue.pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		map((x) => (this._element[this.header] = x)),
		tap(() => {
			this._element.id = this.elementId;
		}),
		switchMap(() => this.warningService.openElementDialog(this._element)),
		switchMap((value) => this.structureService.partialUpdateElement(value))
	);
	private _focus = new Subject<string | null>();
	private _updateValue = combineLatest([this._sendValue, this._focus]).pipe(
		scan(
			(acc, curr) => {
				if (acc.type === curr[1]) {
					acc.count++;
				} else {
					acc.count = 0;
					acc.type = curr[1];
				}
				acc.value = curr[0];
				return acc;
			},
			{ count: 0, type: '', value: undefined } as {
				count: number;
				type: string | null;
				value: element[U] | undefined;
			}
		),
		switchMap((update) =>
			iif(
				() => update.type === null,
				of(true).pipe(
					switchMap(() =>
						this.warningService.openElementDialog(this._element)
					),
					switchMap((value) =>
						this.structureService.partialUpdateElement(value)
					)
				),
				of(false)
			)
		)
	);
	/**
	 * State of when auto complete is initial opened to defer data loading
	 */
	openTypeAutoComplete = new ReplaySubject<void>();

	filteredTypes = this.openTypeAutoComplete.pipe(
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeValue.pipe(
				debounceTime(500),
				map(
					(typeAhead) => (pageNum: string | number) =>
						this.structureService.getPaginatedFilteredTypes(
							this.isString(typeAhead)
								? typeAhead.toLowerCase()
								: (typeAhead as unknown as string),
							this.paginationSize,
							pageNum
						)
				)
			)
		)
	);

	filteredTypesCount = this.openTypeAutoComplete.pipe(
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeValue.pipe(
				debounceTime(500),
				switchMap((search) =>
					this.structureService.getFilteredTypesCount(search)
				)
			)
		)
	);

	private _type: Subject<PlatformType> = new Subject();
	private _sendType = this._type.pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((pType) =>
			of(pType).pipe(
				switchMap((pType) =>
					this.warningService.openElementDialog(this._element)
				),
				switchMap((_) =>
					this.structureService.changeElementPlatformType(
						this.structureId,
						this.elementId,
						pType
					)
				)
			)
		)
	);

	menuPosition = {
		x: '0',
		y: '0',
	};
	constructor(
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structureService: CurrentStructureService,
		private warningService: WarningDialogService
	) {
		this._updateValue.subscribe();
		this._immediateUpdateValue.subscribe();
		this._sendType.subscribe();
		this._updateUnits.subscribe();
	}
	ngOnChanges(changes: SimpleChanges): void {
		if (this.header !== 'platformType') {
			this.updateTypeAhead(this.value);
		} else {
			if (this.isPlatformType(this.value)) {
				this.updateTypeAhead(this.value.name);
			}
		}
		if (
			changes.elementId !== undefined &&
			changes.elementId.previousValue !== changes.elementId.currentValue
		) {
			this._element.id = changes.elementId.currentValue;
		}
	}
	ngOnDestroy(): void {
		this._done.next(true);
	}
	updateElement(header: keyof element, value: element[U]) {
		if (this.header === 'applicability') {
			this.focusChanged('applicability');
		}
		this._value.next(value);
		if (this.header === 'applicability') {
			this.focusChanged(null);
		}
	}
	updateImmediately(header: string, value: element[U]) {
		this._immediateValue.next(value);
	}
	updateType(value: PlatformType) {
		this._type.next(value);
		if (this.isPlatformType(this.value)) {
			this.value.name = value.name;
		}
		this.updateTypeAhead(value.name);
	}

	updateTypeAhead(value: any) {
		this._typeValue.next(value);
	}

	applySearch(searchTerm: string) {
		this._typeValue.next(searchTerm);
	}

	openMenu(event: MouseEvent, location: element[U]) {
		event.preventDefault();
		this.contextMenu.emit(event);
	}
	isString(val: unknown): val is string {
		return typeof val === 'string';
	}
	focusChanged(event: string | null) {
		this._focus.next(event);
	}
	updateUnits(event: string) {
		this._units.next(event);
	}
	autoCompleteOpened() {
		this.openTypeAutoComplete.next();
	}

	/**
	 * Note, this is a hack until we improve the types, don't use unless you know what you are doing
	 */
	isApplic(value: unknown): value is applic {
		return (
			value !== null &&
			value !== undefined &&
			typeof value === 'object' &&
			'id' in value &&
			'name' in value &&
			typeof value.id === 'string' &&
			typeof value.name === 'string'
		);
	}

	/**
	 * Note, this is a hack until we improve the types, don't use unless you know what you are doing
	 */
	returnAsT(value: unknown): element[U] {
		return value as element[U];
	}

	/**
	 * Note, this is a hack until we improve the types, don't use unless you know what you are doing
	 */
	isPlatformType(value: unknown): value is PlatformType {
		return (
			value !== null &&
			value !== undefined &&
			typeof value === 'object' &&
			'id' in value &&
			'name' in value
		);
	}
}
