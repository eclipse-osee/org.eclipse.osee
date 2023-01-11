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
import { Component, Host, Inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { combineLatest, iif, of } from 'rxjs';
import { STRUCTURE_SERVICE_TOKEN } from '../../shared/tokens/injection/structure/token';
import { MULTI_STRUCTURE_SERVICE } from '../../shared/tokens/injection/structure/multi';
import { CurrentStructureService } from '../../shared/services/ui/current-structure.service';
import { StructureTableComponent } from '../lib/tables/structure-table/structure-table.component';
import { AsyncPipe } from '@angular/common';

@Component({
	selector: 'osee-messaging-multi-structure-table-page',
	templateUrl: './multi-structure-table.component.html',
	styleUrls: ['./multi-structure-table.component.sass'],
	standalone: true,
	imports: [StructureTableComponent, AsyncPipe],
})
export class MultiStructureTableComponent implements OnInit, OnDestroy {
	breadCrumb = this.structureService.breadCrumbs;
	constructor(
		private route: ActivatedRoute,
		private router: Router,
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structureService: CurrentStructureService
	) {}
	ngOnDestroy(): void {
		this.structureService.toggleDone = true;
	}

	ngOnInit(): void {
		combineLatest([
			this.route.paramMap,
			this.route.data,
			iif(() => this.router.url.includes('diff'), of(true), of(false)),
		])
			.pipe(takeUntil(this.structureService.done))
			.subscribe(([paramMap, data, mode]) => {
				if (mode) {
					this.structureService.BranchType =
						paramMap.get('branchType') || '';
					this.structureService.branchId =
						paramMap.get('branchId') || '';
					this.structureService.messageId =
						paramMap.get('messageId') || '';
					this.structureService.subMessageId =
						paramMap.get('subMessageId') || '';
					this.structureService.connection =
						paramMap.get('connection') || '';
					this.structureService.singleStructureIdValue = '';
					this.structureService.difference = data?.diff;
					this.structureService.DiffMode = true;
				} else {
					this.structureService.BranchType =
						paramMap.get('branchType') || '';
					this.structureService.branchId =
						paramMap.get('branchId') || '';
					this.structureService.messageId =
						paramMap.get('messageId') || '';
					this.structureService.subMessageId =
						paramMap.get('subMessageId') || '';
					this.structureService.connection =
						paramMap.get('connection') || '';
					this.structureService.BreadCrumb =
						paramMap.get('name') || '';
					this.structureService.singleStructureIdValue = '';
					this.structureService.DiffMode = false;
					this.structureService.difference = [];
				}
			});
	}
}

export default MultiStructureTableComponent;
