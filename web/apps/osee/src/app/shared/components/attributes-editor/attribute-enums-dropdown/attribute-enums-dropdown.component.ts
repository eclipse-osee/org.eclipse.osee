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
import { AsyncPipe } from '@angular/common';
import {
	Component,
	Input,
	OnChanges,
	Output,
	SimpleChanges,
	inject,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatIconButton } from '@angular/material/button';
import {
	ErrorStateMatcher,
	MatOption,
	ShowOnDirtyErrorStateMatcher,
} from '@angular/material/core';
import { MatFormField, MatSuffix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { ArtifactUiService } from '@osee/shared/services';
import { NamedId } from '@osee/shared/types';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import {
	BehaviorSubject,
	ReplaySubject,
	Subject,
	auditTime,
	debounceTime,
	distinctUntilChanged,
	filter,
	switchMap,
} from 'rxjs';
import { MatOptionLoadingComponent } from '../../mat-option-loading/mat-option-loading/mat-option-loading.component';

let nextUniqueId = 0;

@Component({
	selector: 'osee-attribute-enums-dropdown',
	standalone: true,
	imports: [
		AsyncPipe,
		FormsModule,
		MatFormField,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatIcon,
		MatSuffix,
		MatIconButton,
		MatOption,
		MatOptionLoadingComponent,
	],
	templateUrl: './attribute-enums-dropdown.component.html',
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class AttributeEnumsDropdownComponent implements OnChanges {
	@Input() attributeId!: string;
	private _attributeId = new BehaviorSubject<string>('');

	private artifactUiService = inject(ArtifactUiService);

	private _typeAhead = new BehaviorSubject<string>('');
	private _openAutoComplete = new ReplaySubject<void>();

	private _isOpen = new BehaviorSubject<boolean>(false);

	@Input() required = false;
	private _required = new BehaviorSubject<boolean>(false);

	@Input() disabled = false;
	@Input() hintHidden = false;
	@Input() attributeValue = '';

	private _attributeValueChange = new Subject<string>();
	@Output() attributeValueChange = this._attributeValueChange.pipe(
		filter((val) => val != this.attributeValue),
		auditTime(500)
	);

	@Input() errorMatcher: ErrorStateMatcher =
		new ShowOnDirtyErrorStateMatcher();

	protected _enums = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) => this._attributeId),
		switchMap((attributeId) =>
			this.artifactUiService.getAttributeEnums(attributeId)
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
		this._attributeValueChange.next(value);
		this.updateTypeAhead(value);
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (
			changes.attributeValue !== undefined &&
			changes.attributeValue.previousValue !==
				changes.attributeValue.currentValue &&
			changes.attributeValue.currentValue
		) {
			this.updateValue(changes.attributeValue.currentValue);
		}
		if (
			changes.attributeId !== undefined &&
			changes.attributeId.previousValue !==
				changes.attributeId.currentValue &&
			changes.attributeId.currentValue
		) {
			this._attributeId.next(changes.attributeId.currentValue);
		}
		if (
			changes.required !== undefined &&
			changes.required.previousValue !== changes.required.currentValue &&
			changes.required.currentValue
		) {
			this._required.next(changes.required.currentValue);
		}
	}
	get isOpen() {
		return this._isOpen;
	}
	clear() {
		this.updateTypeAhead('');
	}

	protected _componentId = `${nextUniqueId++}`;
}
