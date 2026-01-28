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
import { CommonModule } from '@angular/common';
import {
	// MatAutocomplete,
	MatAutocompleteSelectedEvent,
	// MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatMenuModule } from '@angular/material/menu'; // Author: Kris Graham (kgraha16) Task 122 - Added MatMenu to stylize Column button.
import { MatButtonModule } from '@angular/material/button'; // Author: Kris Graham (kgraha16) Task 112 - Added MatButton to stylize New Search.
import { MatDividerModule } from '@angular/material/divider'; // Author: Kris Graham (kgraha16) Task 131 - Added MatDivider to divide Columns menu.
import { MatCheckboxModule } from '@angular/material/checkbox';
// import { MatChip, MatChipRemove, MatChipSet } from '@angular/material/chips';
// import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatIconButton } from '@angular/material/button';
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
		CommonModule,
		MatFormField,
		MatLabel,
		// MatChipSet,
		// MatChip,
		// MatChipRemove,
		MatInput,
		MatSuffix,
		MatIconButton, 
		// MatAutocomplete,
		// MatAutocompleteTrigger,
		// MatOption,
		MatCheckboxModule,
		MatButtonModule, // Author: Kris Graham (kgraha16) Task 112 - Added MatButton to stylize New Search.
		MatMenuModule, // Author: Kris Graham (kgraha16) Task 122 - Added MatStrokedButton to stylize Column button.
		MatDividerModule, // Author: Kris Graham (kgraha16) Task 131 - Added MatDivider to divide Columns menu.
		MatIconModule,
	],
	templateUrl: './advanced-search-form.component.html',
})

export class AdvancedSearchFormComponent {
	private artifactService = inject(ArtifactUiService);

	@Input() data: AdvancedSearchCriteria = {
		...defaultAdvancedSearchCriteria,
	};

	searchValue = '';
	searchResults: any[] = [];  // Author: Sofiia Holovko (sholovko) Task 145 - Handle "no results found" state

	public showSearchError: boolean = false;

	/** 
	* Author: Kris Graham (kgraha16)
	* Task 131 - Create base available columns for Column customization button.
	*/
	baseColumns = [
		{ key: 'id', label: 'ID', visible: true, locked: true },
		{ key: 'name', label: 'Name', visible: true, locked: false },
		{ key: 'type', label: 'Type', visible: true, locked: false }
	];
	
	/** 
		* Author: Kris Graham (kgraha16)
		* Task 131 - Create available attribute columns for Column customization button.
		*/
	attributeColumns = computed(() =>
		this.allAttributeTypes().map(attr => ({
			key: `attr_${attr.id}`,
			label: attr.name,
			visible: false,
		}))
	);
	
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
	
	/** 
		* Author: Kris Graham (kgraha16)
		* Task 131 - Create signal to get all attribute types for Columns menu checkboxes.
		*/
	allAttributeTypes = toSignal(
		this.artifactService.allAttributeTypes,
		{ initialValue: [] }
	);

	compareWith(o1: NamedId, o2: NamedId) {
		return o1.id === o2.id;
	}
	displayWith(val: NamedId) {
		return val?.name;
	}
	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 107 - Create save button for Advanced Search Options
	 *
	 * Placeholder handler for the Save Search button.
	 * Future work: integrate with a service to persist the current criteria.
	 */
	onSaveSearch(): void {
		// For now just log the current criteria so we can verify the wiring.
		console.log('Save Search clicked with criteria:', this.data);
	}

	/**
	 * Author: Daria Berezianska (dvydybor)
	 * Task 125 - Change the input alarm from being a popup to being a red written text under the search input bar
	 * 
	 * Handler for the search button in the Advanced Search Options modal.
	 * If the search field is empty show an alert under the field prompting the user.
	 */
	onSearch(): void {
		if (!this.searchValue || this.searchValue.trim().length === 0) {
			// show inline error under the field instead of a blocking alert
			this.showSearchError = true;
			return;
		}
		this.showSearchError = false;
	}

	onSearchValueChange(): void {
		if (this.searchValue && this.searchValue.trim().length > 0) {
			this.showSearchError = false;
		}
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 128 - Add clear (X) action to Advanced Search input
	 *
	 * Clears the search field and hides any inline error.
	 */
	clearSearch(): void {
	this.searchValue = '';
	this.showSearchError = false;
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 129 - Add clear (X) action to Search Title input
	 *
	 * Clears the Search Title field.
	 */
	clearSearchTitle(): void {
		this.data.searchTitle = '';
	}
	
	/** 
	 * Author: Kris Graham (kgraha16)
	 * Task 113 - Create functionality for clicking New Search button in
	 * the advanced search form.
	 */
	onNewSearch(): void {
		this.data={...defaultAdvancedSearchCriteria};
		this.searchValue='';
		this.data.searchTitle='';
		
		//Author: Sofiia Holovko (sholovko) Task 145 - Clear search results on new search
		 this.searchResults=[];
	}
}