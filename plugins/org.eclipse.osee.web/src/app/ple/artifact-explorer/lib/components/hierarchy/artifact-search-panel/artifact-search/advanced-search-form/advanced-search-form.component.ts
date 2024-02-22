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
import { Component, Input, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import {
	MatAutocompleteModule,
	MatAutocompleteSelectedEvent,
} from '@angular/material/autocomplete';
import {
	AdvancedSearchCriteria,
	defaultAdvancedSearchCriteria,
} from '../../../../../types/artifact-search';
import { NamedId } from '@osee/shared/types';
import { toSignal } from '@angular/core/rxjs-interop';
import { BehaviorSubject, switchMap } from 'rxjs';
import { ArtifactUiService } from '@osee/shared/services';

@Component({
	selector: 'osee-advanced-search-form',
	standalone: true,
	imports: [
		FormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatCheckboxModule,
		MatIconModule,
		MatChipsModule,
		MatAutocompleteModule,
	],
	templateUrl: './advanced-search-form.component.html',
})
export class AdvancedSearchFormComponent {
	@Input() data: AdvancedSearchCriteria = {
		...defaultAdvancedSearchCriteria,
	};

	constructor(private artifactService: ArtifactUiService) {}

	artifactTypes = toSignal(this.artifactService.artifactTypes);
	_selectedArtifactTypes = new BehaviorSubject<NamedId[]>([]);
	artTypesFilter = signal('');
	filteredArtTypes = computed(
		() =>
			this.artifactTypes()?.filter((a) =>
				a.name
					.toLowerCase()
					.includes(this.artTypesFilter().toLowerCase())
			)
	);
	updateArtTypesFilter(value: string) {
		this.artTypesFilter.set(value);
	}
	selectArtType(event: MatAutocompleteSelectedEvent) {
		if (
			this.data.artifactTypes.filter(
				(a) => a.id === event.option.value.id
			).length === 0
		) {
			this.data.artifactTypes.push(event.option.value);
		}
		this.artTypesFilter.set('');
		this._selectedArtifactTypes.next(this.data.artifactTypes);
	}
	removeArtType(artType: NamedId) {
		this.data.artifactTypes = this.data.artifactTypes.filter(
			(a) => a.id !== artType.id
		);
		this.artTypesFilter.set('');
		this._selectedArtifactTypes.next(this.data.artifactTypes);
	}

	attributeTypes = toSignal(
		this._selectedArtifactTypes.pipe(
			switchMap((artTypes) =>
				this.artifactService.getAttributeTypes(artTypes)
			)
		)
	);
	attrTypesFilter = signal('');
	filteredAttrTypes = computed(
		() =>
			this.attributeTypes()?.filter((a) =>
				a.name
					.toLowerCase()
					.includes(this.attrTypesFilter().toLowerCase())
			)
	);
	updateAttrTypesFilter(value: string) {
		this.attrTypesFilter.set(value);
	}
	selectAttrType(event: MatAutocompleteSelectedEvent) {
		if (
			this.data.attributeTypes.filter(
				(a) => a.id === event.option.value.id
			).length === 0
		) {
			this.data.attributeTypes.push(event.option.value);
		}
		this.attrTypesFilter.set('');
	}
	removeAttrType(attrType: NamedId) {
		this.data.attributeTypes = this.data.attributeTypes.filter(
			(a) => a.id !== attrType.id
		);
		this.attrTypesFilter.set('');
	}

	compareWith(o1: NamedId, o2: NamedId) {
		return o1.id === o2.id;
	}
	displayWith(val: NamedId) {
		return val?.name;
	}
}
