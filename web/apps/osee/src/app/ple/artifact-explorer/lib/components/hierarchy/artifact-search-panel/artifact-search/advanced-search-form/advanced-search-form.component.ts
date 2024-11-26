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
import { Component, Input, computed, signal, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteSelectedEvent,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatChip, MatChipRemove, MatChipSet } from '@angular/material/chips';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { ArtifactUiService } from '@osee/shared/services';
import { NamedId } from '@osee/shared/types';
import { BehaviorSubject, switchMap } from 'rxjs';
import {
	AdvancedSearchCriteria,
	defaultAdvancedSearchCriteria,
} from '../../../../../types/artifact-search';

@Component({
	selector: 'osee-advanced-search-form',
	imports: [
		FormsModule,
		MatFormField,
		MatLabel,
		MatChipSet,
		MatChip,
		MatChipRemove,
		MatIcon,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
		MatCheckbox,
	],
	templateUrl: './advanced-search-form.component.html',
})
export class AdvancedSearchFormComponent {
	private artifactService = inject(ArtifactUiService);

	@Input() data: AdvancedSearchCriteria = {
		...defaultAdvancedSearchCriteria,
	};

	artifactTypes = toSignal(this.artifactService.allArtifactTypes);
	_selectedArtifactTypes = new BehaviorSubject<NamedId[]>([]);
	artTypesFilter = signal('');
	filteredArtTypes = computed(() =>
		this.artifactTypes()?.filter((a) =>
			a.name.toLowerCase().includes(this.artTypesFilter().toLowerCase())
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
	filteredAttrTypes = computed(() =>
		this.attributeTypes()?.filter((a) =>
			a.name.toLowerCase().includes(this.attrTypesFilter().toLowerCase())
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
