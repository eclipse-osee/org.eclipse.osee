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
	computed,
	effect,
	inject,
	linkedSignal,
	signal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { filter, switchMap } from 'rxjs';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatIconButton } from '@angular/material/button';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { MatOption } from '@angular/material/core';
import {
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { CiDashboardUiService } from '../../../services/ci-dashboard-ui.service';
import { CiSetRoutedUiService } from '../../../services/ci-set-routed-ui.service';
import { CiSetsService } from '../../../services/ci-sets.service';
import type { CISet } from '../../../types/tmo';

@Component({
	selector: 'osee-set-dropdown',
	templateUrl: './set-dropdown.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		FormsModule,
		MatCheckbox,
		MatFormField,
		MatLabel,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
		MatSuffix,
		MatIconButton,
		MatIcon,
		MatProgressSpinner,
		MatTooltip,
	],
})
export class SetDropdownComponent {
	private ciSetsService = inject(CiSetsService);
	private ui = inject(CiDashboardUiService);
	private setRouteService = inject(CiSetRoutedUiService);

	filterText = signal('');
	// True while the input shows typed filter text instead of the selected set name.
	private typing = signal(false);
	private preventDefaultSelection = signal(false);
	private ciSetId = toSignal(this.ui.ciSetId, { initialValue: '-1' });
	private routeSetId = computed(() => {
		const setId = this.ciSetId();
		return setId !== undefined && setId !== '-1' ? setId : undefined;
	});
	// The full list is needed only with no set in the URL (default selection) or once the
	// user focuses/types in the field. Keyed on the URL signal so the initial '-1' before
	// the URL sync does not fetch. Sticky so the list survives selecting a set.
	private listRequested = linkedSignal<boolean, boolean>({
		source: () => this.setRouteService.urlSetId() === '-1',
		computation: (noSetInUrl, previous) =>
			noSetInUrl || (previous?.value ?? false),
	});
	private ciSets = toSignal(
		toObservable(this.listRequested).pipe(
			filter(Boolean),
			switchMap(() => this.ciSetsService.ciSets)
		)
	);
	private selectedSetResource = this.ciSetsService.getCiSetResource(
		this.routeSetId
	);
	sets = computed(() => {
		const filterText = this.filterText().toLowerCase();
		return (this.ciSets() ?? []).filter((set) =>
			set.name.value.toLowerCase().includes(filterText)
		);
	});
	activeOnly = toSignal(this.ciSetsService.activeOnly);
	listLoading = computed(
		() => this.listRequested() && this.ciSets() === undefined
	);
	// An unknown id returns HTTP 200 with a sentinel (id -1), so the id must match.
	private routeSetById = computed(() => {
		const set = this.selectedSetResource.hasValue()
			? this.selectedSetResource.value()
			: undefined;
		return set !== undefined && set.id === this.routeSetId()
			? set
			: undefined;
	});
	selectedSet = computed(() => {
		if (this.preventDefaultSelection()) {
			return undefined;
		}
		const setId = this.routeSetId();
		if (setId === undefined) {
			return this.sets()[0];
		}
		return (
			this.routeSetById() ??
			this.ciSets()?.find((set) => set.id === setId)
		);
	});
	private defaultSelectionEffect = effect(() => {
		const selectedSet = this.selectedSet();
		if (
			selectedSet &&
			this.routeSetId() === undefined &&
			!this.preventDefaultSelection()
		) {
			this.setRouteService.setCISetAndNavigate(selectedSet.id);
		}
	});
	private routeSelectionEffect = effect(() => {
		if (this.routeSetId() !== undefined) {
			this.preventDefaultSelection.set(false);
		}
	});
	loadingSelectedSet = computed(
		() =>
			this.routeSetId() !== undefined &&
			this.selectedSetResource.isLoading() &&
			this.selectedSet() === undefined
	);
	selectedSetError = computed(
		() =>
			this.routeSetId() !== undefined &&
			!this.preventDefaultSelection() &&
			this.selectedSetResource.error() !== undefined
	);
	missingSelectedSet = computed(
		() =>
			this.routeSetId() !== undefined &&
			!this.preventDefaultSelection() &&
			!this.selectedSetResource.isLoading() &&
			this.selectedSetResource.hasValue() &&
			this.routeSetById() === undefined
	);
	statusText = computed(() => {
		if (this.loadingSelectedSet()) {
			return 'Loading set…';
		}
		if (this.selectedSetError()) {
			return 'Unable to load set.';
		}
		return this.missingSelectedSet() ? 'Set not found on this branch.' : '';
	});
	inputText = computed(() =>
		this.typing()
			? this.filterText()
			: (this.selectedSet()?.name?.value ?? '')
	);
	requestList() {
		this.listRequested.set(true);
	}
	setActiveOnly(event: MatCheckboxChange) {
		this.ciSetsService.ActiveOnly = event.checked;
	}

	selectSet(set: CISet) {
		this.preventDefaultSelection.set(false);
		this.typing.set(false);
		this.filterText.set('');
		this.setRouteService.setCISetAndNavigate(set.id);
	}

	// Bound to mousedown (keeps the autocomplete panel from closing first) and Enter/Space.
	clearSelection(event: Event) {
		event.preventDefault();
		event.stopPropagation();
		this.preventDefaultSelection.set(true);
		this.typing.set(false);
		this.filterText.set('');
		this.setRouteService.setCISetAndNavigate('-1');
	}

	applyFilter(event: Event) {
		this.requestList();
		this.typing.set(true);
		this.filterText.set((event.target as HTMLInputElement).value);
	}
}
