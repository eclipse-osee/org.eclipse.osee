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
import { AsyncPipe, NgIf, NgFor } from '@angular/common';
import {
	Component,
	Optional,
	Input,
	inject,
	Output,
	SimpleChanges,
	OnChanges,
} from '@angular/core';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
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
	combineLatest,
} from 'rxjs';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';

function controlContainerFactory(controlContainer?: ControlContainer) {
	return controlContainer;
}

@Component({
	selector: 'osee-attribute-enums-dropdown',
	standalone: true,
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
		NgFor,
	],
	templateUrl: './attribute-enums-dropdown.component.html',
	viewProviders: [
		{
			provide: ControlContainer,
			useFactory: controlContainerFactory,
			deps: [[new Optional(), NgForm]],
		},
	],
})
export class AttributeEnumsDropdownComponent implements OnChanges {
	@Input() artifactId!: string;
	@Input() attributeId!: string;
	@Input() branchId!: string;

	private _artifactId = new BehaviorSubject<string>('');
	private _attributeId = new BehaviorSubject<string>('');
	private _branchId = new BehaviorSubject<string>('');

	private _artExpHttpService = inject(ArtifactExplorerHttpService);

	private _typeAhead = new BehaviorSubject<string>('');
	private _openAutoComplete = new ReplaySubject<void>();

	private _isOpen = new BehaviorSubject<boolean>(false);

	@Input() required: boolean = false;
	@Input() disabled: boolean = false;

	@Input() hintHidden: boolean = false;
	@Input() attributeValue: string = '';

	private _attributeValueChange = new Subject<string>();
	@Output() attributeValueChange = this._attributeValueChange.pipe(skip(1));

	@Input() errorMatcher: ErrorStateMatcher =
		new ShowOnDirtyErrorStateMatcher();

	protected _enums = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			combineLatest([this._branchId, this._artifactId, this._attributeId])
		),
		switchMap(([branchId, artifactId, attributeId]) =>
			this._artExpHttpService.getAttributeEnums(
				branchId,
				artifactId,
				attributeId
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
		this._attributeValueChange.next(value);
		this.updateTypeAhead(value);
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (
			changes.attributeValue !== undefined &&
			changes.attributeValue.previousValue !==
				changes.attributeValue.currentValue &&
			changes.attributeValue.currentValue !== undefined
		) {
			this.updateValue(changes.attributeValue.currentValue);
		}
		if (
			changes.artifactId !== undefined &&
			changes.artifactId.previousValue !==
				changes.artifactId.currentValue &&
			changes.artifactId.currentValue !== undefined
		) {
			this._artifactId.next(changes.artifactId.currentValue);
		}
		if (
			changes.branchId !== undefined &&
			changes.branchId.previousValue !== changes.branchId.currentValue &&
			changes.branchId.currentValue !== undefined
		) {
			this._branchId.next(changes.branchId.currentValue);
		}
		if (
			changes.attributeId !== undefined &&
			changes.attributeId.previousValue !==
				changes.attributeId.currentValue &&
			changes.attributeId.currentValue !== undefined
		) {
			this._attributeId.next(changes.attributeId.currentValue);
		}
	}
	get isOpen() {
		return this._isOpen;
	}
	clear() {
		this.updateTypeAhead('');
	}
}
