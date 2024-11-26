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
import { Component, signal, viewChild, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatChip, MatChipRemove, MatChipSet } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import {
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { UiService } from '@osee/shared/services';
import { NamedId } from '@osee/shared/types';
import {
	BehaviorSubject,
	combineLatest,
	debounceTime,
	filter,
	map,
	switchMap,
	take,
	tap,
} from 'rxjs';
import { AdvancedArtifactSearchService } from '../../../../services/advanced-artifact-search.service';
import { ArtifactExplorerHttpService } from '../../../../services/artifact-explorer-http.service';
import { ArtifactExplorerTabService } from '../../../../services/artifact-explorer-tab.service';
import { ArtifactHierarchyPathService } from '../../../../services/artifact-hierarchy-path.service';
import { ArtifactIconService } from '../../../../services/artifact-icon.service';
import { AdvancedSearchCriteria } from '../../../../types/artifact-search';
import { AdvancedSearchDialogComponent } from './advanced-search-dialog/advanced-search-dialog.component';
import { PaginatedMatListComponent } from '../../../shared/paginated-mat-list/paginated-mat-list.component';
import {
	artifactTokenWithIcon,
	artifactTypeIcon,
} from '@osee/artifact-with-relations/types';

@Component({
	selector: 'osee-artifact-search',
	imports: [
		AsyncPipe,
		FormsModule,
		MatChipSet,
		MatChip,
		MatTooltip,
		MatChipRemove,
		MatIcon,
		MatFormField,
		MatLabel,
		MatInput,
		MatSuffix,
		MatIconButton,
		MatMenu,
		MatMenuContent,
		MatMenuItem,
		MatMenuTrigger,
		PaginatedMatListComponent,
	],
	templateUrl: './artifact-search.component.html',
})
export class ArtifactSearchComponent {
	private artExpHttpService = inject(ArtifactExplorerHttpService);
	private uiService = inject(UiService);
	private artHierPathService = inject(ArtifactHierarchyPathService);
	dialog = inject(MatDialog);
	private advancedSearchService = inject(AdvancedArtifactSearchService);
	private tabService = inject(ArtifactExplorerTabService);
	private artifactIconService = inject(ArtifactIconService);

	matMenuTrigger = viewChild.required(MatMenuTrigger);

	searchText = new BehaviorSubject<string>('');
	searchTrigger = new BehaviorSubject<boolean>(false);

	branchId = toSignal(this.uiService.id);
	branchType = toSignal(this.uiService.type);
	viewId = toSignal(this.uiService.viewId);
	advancedSearchCriteria = this.advancedSearchService.advancedSearchCriteria;

	pageNum = new BehaviorSubject<number>(1);
	pageSize = 100;
	menuPosition = {
		x: '0',
		y: '0',
	};

	searchDisabled = combineLatest([
		this.searchText,
		this.advancedSearchCriteria,
	]).pipe(
		map(
			([filter, criteria]) =>
				(filter === '' && criteria.artifactTypes.length === 0) ||
				(filter === '' && criteria.attributeTypes.length > 0)
		)
	);

	allSearchResults = signal<artifactTokenWithIcon[]>([]);

	useCount = toSignal(
		this.advancedSearchCriteria.pipe(
			map((criteria) => criteria.artifactTypes.length > 0)
		)
	);

	paginatedSearchResults = combineLatest([
		this.pageNum,
		this.uiService.id,
		this.uiService.viewId,
		this.searchText,
		this.advancedSearchCriteria,
		this.searchTrigger,
	]).pipe(
		filter(
			([_, branchId, viewId, filter, criteria, trigger]) =>
				branchId != '-1' &&
				branchId != '0' &&
				branchId != '' &&
				viewId != '' &&
				!(filter === '' && criteria.artifactTypes.length === 0) &&
				!(filter === '' && criteria.attributeTypes.length > 0) &&
				trigger === true
		),
		debounceTime(200),
		switchMap(([pageNum, branchId, viewId, filter, criteria, _]) =>
			this.artExpHttpService
				.getArtifactTokensByFilter(
					branchId,
					filter,
					viewId,
					this.pageSize,
					pageNum,
					criteria
				)
				.pipe(
					tap((results) => {
						this.allSearchResults.update((current) => [
							...current,
							...results,
						]);
					})
				)
		)
	);

	searchResultsCount = combineLatest([
		this.uiService.id,
		this.uiService.viewId,
		this.searchText,
		this.advancedSearchCriteria,
		this.searchTrigger,
	]).pipe(
		filter(
			([branchId, viewId, filter, criteria, trigger]) =>
				criteria.artifactTypes.length > 0 &&
				branchId != '-1' &&
				branchId != '0' &&
				branchId != '' &&
				viewId != '' &&
				!(filter === '' && criteria.artifactTypes.length === 0) &&
				!(filter === '' && criteria.attributeTypes.length > 0) &&
				trigger === true
		),
		debounceTime(200),
		switchMap(([branchId, viewId, filter, criteria, _]) =>
			this.artExpHttpService.getArtifactsByFilterCount(
				branchId,
				filter,
				viewId,
				criteria
			)
		)
	);

	performSearch(e: Event) {
		e.stopPropagation();
		this.allSearchResults.set([]);
		this.pageNum.next(1);
		this.searchTrigger.next(true);
		this.searchTrigger.next(false);
	}

	selectSearchResult(artifact: artifactTokenWithIcon) {
		if (
			this.tabService
				.Tabs()
				.filter(
					(t) =>
						t.tabType === 'Artifact' &&
						t.artifact.id === artifact.id
				).length > 0
		) {
			return;
		}
		const branchId = this.branchId();
		const branchType = this.branchType();
		if (branchId && branchType && artifact.id !== '-1') {
			this.uiService.viewId
				.pipe(
					switchMap((viewId) =>
						this.artExpHttpService
							.getartifactWithRelations(
								branchId,
								artifact.id,
								viewId,
								true
							)
							.pipe(
								tap((art) =>
									this.tabService.addArtifactTab({
										...art,
										editable: branchType !== 'baseline',
									})
								)
							)
					)
				)
				.subscribe();
		}
	}

	updateFilter(event: KeyboardEvent) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.searchText.next(filterValue);
	}

	removeArtTypeFilter(currentCriteria: AdvancedSearchCriteria) {
		this.advancedSearchService.AdvancedSearchCriteria = {
			...currentCriteria,
			artifactTypes: [],
		};
	}

	removeAttrTypeFilter(currentCriteria: AdvancedSearchCriteria) {
		this.advancedSearchService.AdvancedSearchCriteria = {
			...currentCriteria,
			attributeTypes: [],
		};
	}

	removeExactMatchFilter(currentCriteria: AdvancedSearchCriteria) {
		this.advancedSearchService.AdvancedSearchCriteria = {
			...currentCriteria,
			exactMatch: false,
		};
	}

	removeSearchByIdFilter(currentCriteria: AdvancedSearchCriteria) {
		this.advancedSearchService.AdvancedSearchCriteria = {
			...currentCriteria,
			searchById: false,
		};
	}

	getToolTip(vals: NamedId[]) {
		return vals.map((v) => v.name).join('\n');
	}

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifactIconService.getIconClass(icon) +
			' ' +
			this.artifactIconService.getIconVariantClass(icon)
		);
	}

	showInHierarchy(artifact: artifactTokenWithIcon) {
		this.artHierPathService.updatePaths(artifact.id);
	}

	openContextMenu(event: MouseEvent, artifact: artifactTokenWithIcon) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		this.matMenuTrigger().menuData = {
			artifact: artifact,
		};
		this.matMenuTrigger().openMenu();
	}

	nextPage() {
		this.pageNum.next(this.pageNum.getValue() + 1);
		this.searchTrigger.next(true);
		this.searchTrigger.next(false);
	}

	openAdvancedSearchDialog(event?: Event) {
		event?.stopPropagation();
		this.advancedSearchCriteria
			.pipe(
				take(1),
				switchMap((criteria) =>
					this.dialog
						.open(AdvancedSearchDialogComponent, {
							data: structuredClone(criteria),
							minWidth: '40%',
							width: '40%',
						})
						.afterClosed()
						.pipe(
							take(1),
							filter((v) => v !== undefined),
							tap(
								(newCriteria) =>
									(this.advancedSearchService.AdvancedSearchCriteria =
										newCriteria)
							)
						)
				)
			)
			.subscribe();
	}
}
