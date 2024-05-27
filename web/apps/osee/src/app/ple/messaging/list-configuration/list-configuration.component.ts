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
import { Component, inject, OnDestroy } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import {
	MessagingControlsComponent,
	NamedIdListEditorComponent,
} from '@osee/messaging/shared/main-content';
import { UiService } from '@osee/shared/services';
import { filter, Subject, takeUntil, tap } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { PageEvent } from '@angular/material/paginator';
import { NamedId } from '@osee/shared/types';
import { CurrentViewSelectorComponent } from '@osee/shared/components';
import { toSignal } from '@angular/core/rxjs-interop';
import { CurrentUnitsService } from '@osee/messaging/units/services';
import { CurrentStructureCategoriesService } from '@osee/messaging/structure-category/services';
import { CurrentMessagePeriodicityService } from '@osee/messaging/message-periodicity/services';
import { CurrentMessageTypesService } from '@osee/messaging/message-type/services';
import { CurrentRatesService } from '@osee/messaging/rate/services';

@Component({
	selector: 'osee-list-configuration',
	standalone: true,
	imports: [
		NamedIdListEditorComponent,
		MessagingControlsComponent,
		CurrentViewSelectorComponent,
		AsyncPipe,
	],
	templateUrl: './list-configuration.component.html',
})
export class ListConfigurationComponent implements OnDestroy {
	private _uiService = inject(UiService);

	private _route = inject(ActivatedRoute);

	private _currentUnitsService = inject(CurrentUnitsService);

	private _currentRatesService = inject(CurrentRatesService);

	private _currentMessageTypesService = inject(CurrentMessageTypesService);

	private _currentStructureCategoriesService = inject(
		CurrentStructureCategoriesService
	);
	private _currentMessagePeriodicityService = inject(
		CurrentMessagePeriodicityService
	);
	branchId = toSignal(
		this._uiService.id.pipe(
			filter((id) => id !== '' && id !== '-1' && id !== '0')
		)
	);
	branchType = toSignal(this._uiService.type);
	private _destroyed = new Subject<void>();
	routeSetup = this._route.paramMap.pipe(
		tap((params) => {
			this._uiService.idValue = params.get('branchId') || '';
			this._uiService.typeValue =
				(params.get('branchType') as 'working' | 'baseline' | '') || '';
		}),
		takeUntil(this._destroyed)
	);
	setup = this.routeSetup.subscribe();
	units = this._currentUnitsService.current;
	unitsCount = this._currentUnitsService.count;
	unitsPageSize = this._currentUnitsService.currentPageSize;
	unitsPageIndex = this._currentUnitsService.currentPage;
	ngOnDestroy(): void {
		this._destroyed.next();
	}
	updateUnitsPages(pg: PageEvent) {
		this._currentUnitsService.page = pg.pageIndex;
		this._currentUnitsService.pageSize = pg.pageSize;
	}
	unitsFilterChange(f: string) {
		this._currentUnitsService.page = 0;
		this._currentUnitsService.filter = f;
	}
	updateUnit(value: NamedId) {
		this._currentUnitsService.modifyUnit(value).subscribe();
	}

	createUnit(value: string) {
		this._currentUnitsService.createUnit(value).subscribe();
	}

	rates = this._currentRatesService.current;
	ratesCount = this._currentRatesService.count;
	ratesPageSize = this._currentRatesService.currentPageSize;
	ratesPageIndex = this._currentRatesService.currentPage;

	updateRatesPages(pg: PageEvent) {
		this._currentRatesService.page = pg.pageIndex;
		this._currentRatesService.pageSize = pg.pageSize;
	}
	ratesFilterChange(f: string) {
		this._currentRatesService.page = 0;
		this._currentRatesService.filter = f;
	}
	updateRate(value: NamedId) {
		this._currentRatesService.modifyRate(value).subscribe();
	}

	createRate(value: string) {
		this._currentRatesService.createRate(value).subscribe();
	}

	messageTypes = this._currentMessageTypesService.current;
	messageTypesCount = this._currentMessageTypesService.count;
	messageTypesPageSize = this._currentMessageTypesService.currentPageSize;
	messageTypesPageIndex = this._currentMessageTypesService.currentPage;

	updateMessageTypesPages(pg: PageEvent) {
		this._currentMessageTypesService.page = pg.pageIndex;
		this._currentMessageTypesService.pageSize = pg.pageSize;
	}
	messageTypesFilterChange(f: string) {
		this._currentMessageTypesService.page = 0;
		this._currentMessageTypesService.filter = f;
	}
	updateMessageType(value: NamedId) {
		this._currentMessageTypesService.modifyMessageType(value).subscribe();
	}

	createMessageType(value: string) {
		this._currentMessageTypesService.createMessageType(value).subscribe();
	}

	structureCategories = this._currentStructureCategoriesService.current;
	structureCategoriesCount = this._currentStructureCategoriesService.count;
	structureCategoriesPageSize =
		this._currentStructureCategoriesService.currentPageSize;
	structureCategoriesPageIndex =
		this._currentStructureCategoriesService.currentPage;

	updateStructureCategoriesPages(pg: PageEvent) {
		this._currentStructureCategoriesService.page = pg.pageIndex;
		this._currentStructureCategoriesService.pageSize = pg.pageSize;
	}
	structureCategoriesFilterChange(f: string) {
		this._currentStructureCategoriesService.page = 0;
		this._currentStructureCategoriesService.filter = f;
	}
	updateStructureCategory(value: NamedId) {
		this._currentStructureCategoriesService
			.modifyStructureCategory(value)
			.subscribe();
	}

	createStructureCategory(value: string) {
		this._currentStructureCategoriesService
			.createStructureCategory(value)
			.subscribe();
	}
	messagePeriodicities = this._currentMessagePeriodicityService.current;
	messagePeriodicitiesCount = this._currentMessagePeriodicityService.count;
	messagePeriodicitiesPageSize =
		this._currentMessagePeriodicityService.currentPageSize;
	messagePeriodicitiesPageIndex =
		this._currentMessagePeriodicityService.currentPage;

	updateMessagePeriodicitiesPages(pg: PageEvent) {
		this._currentMessagePeriodicityService.page = pg.pageIndex;
		this._currentMessagePeriodicityService.pageSize = pg.pageSize;
	}
	messagePeriodicitiesFilterChange(f: string) {
		this._currentMessagePeriodicityService.page = 0;
		this._currentMessagePeriodicityService.filter = f;
	}
	updateMessagePeriodicity(value: NamedId) {
		this._currentMessagePeriodicityService
			.modifyMessagePeriodicity(value)
			.subscribe();
	}

	createMessagePeriodicity(value: string) {
		this._currentMessagePeriodicityService
			.createMessagePeriodicity(value)
			.subscribe();
	}
}

export default ListConfigurationComponent;
