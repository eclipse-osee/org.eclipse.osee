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
import { ActivatedRoute, ParamMap } from '@angular/router';
import { combineLatest, iif, of } from 'rxjs';
import { repeatWhen, switchMap, takeUntil, tap } from 'rxjs/operators';
import { HttpLoadingService } from '../../../shared/services/ui/http-loading.service';
import { CurrentStateService } from '../../services/current-state.service';
import { structure } from '../../types/structure';

@Component({
  selector: 'app-single-structure-table',
  templateUrl: './single-structure-table.component.html',
  styleUrls: ['./single-structure-table.component.sass']
})
export class SingleStructureTableComponent implements OnInit, OnDestroy {
  isLoading = this.loadingService.isLoading;
  messageData = of(new MatTableDataSource<structure>())
  breadCrumb: string = '';

  constructor(private route: ActivatedRoute,
    private structureService: CurrentStateService,
    private loadingService: HttpLoadingService,) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((values) => {
      this.breadCrumb = values.get('name') || '';
      this.structureService.BranchType = values.get('branchType') || '';
      this.structureService.branchId = values.get('branchId') || '';
      this.structureService.messageId = values.get('messageId') || '';
      this.structureService.subMessageId = values.get('subMessageId') || '';
      this.structureService.connection = values.get('connection') || '';
      this.messageData = this.structureService.getStructureRepeating(values.get('structureId') || '').pipe(
        switchMap((data) => of(new MatTableDataSource<structure>([data]))),
        takeUntil(this.structureService.done)
      )
    });
  }
  ngOnDestroy(): void {
    this.structureService.toggleDone = true;
  }
}
