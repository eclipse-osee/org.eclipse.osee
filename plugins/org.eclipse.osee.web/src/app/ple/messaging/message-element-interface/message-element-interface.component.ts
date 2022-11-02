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
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Data, NavigationEnd, Router } from '@angular/router';
import { CurrentStructureService } from './services/current-structure.service';
import { structure } from '../shared/types/structure';
import { filter, map, switchMap, take, takeUntil, tap } from 'rxjs/operators';
import { combineLatest, iif, of } from 'rxjs';

@Component({
	selector: 'osee-messaging-message-element-interface',
	templateUrl: './message-element-interface.component.html',
	styleUrls: ['./message-element-interface.component.sass'],
})
export class MessageElementInterfaceComponent implements OnInit, OnDestroy {
	tableData = of(new MatTableDataSource<structure>());

	messageData = combineLatest([
		this.tableData,
		this.structureService.structures,
	]).pipe(
		map(([table, structures]) => {
			table.data = structures;
			return table;
		}),
		takeUntil(this.structureService.done)
	);
	breadCrumb = this.structureService.breadCrumbs;
	constructor(
		private route: ActivatedRoute,
		private router: Router,
		public dialog: MatDialog,
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
