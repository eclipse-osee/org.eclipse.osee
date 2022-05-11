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
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Data, NavigationEnd, ParamMap, Router } from '@angular/router';
import { combineLatest, iif, of } from 'rxjs';
import { filter, map, repeatWhen, switchMap, take, takeUntil, tap } from 'rxjs/operators';
import { CurrentStructureService } from '../../services/current-structure.service';
import { structure, structureWithChanges } from '../../../shared/types/structure';

@Component({
  selector: 'app-single-structure-table',
  templateUrl: './single-structure-table.component.html',
  styleUrls: ['./single-structure-table.component.sass']
})
export class SingleStructureTableComponent implements OnInit, OnDestroy {
  messageData = of(new MatTableDataSource<structure|structureWithChanges>())
  breadCrumb = this.structureService.breadCrumbs;
  structureId = this.structureService.singleStructureId;

  constructor (private route: ActivatedRoute,
    private router:Router,
    private structureService: CurrentStructureService,) {
     }

  ngOnInit(): void {
    of(this.route).pipe(
      switchMap(route => {
        while (route.parent && !route.snapshot.paramMap.has('structureId')) {
          route = route.parent;
        }
        return of(route);
      }),
      filter(activatedRoute => activatedRoute.outlet === 'primary'),
      switchMap((route) => combineLatest([route.paramMap, route.firstChild?.data || route.url]).pipe(
        map(([paramMap, data]) => {
          this.structureService.BranchType = paramMap.get('branchType') || '';
          this.structureService.branchId = paramMap.get('branchId') || '';
          this.structureService.messageId = paramMap.get('messageId') || '';
          this.structureService.subMessageId = paramMap.get('subMessageId') || '';
          this.structureService.connection = paramMap.get('connection') || '';
          this.structureService.singleStructureIdValue = paramMap.get('structureId') || '';
          this.structureService.BreadCrumb = paramMap.get('name') || '';
          return data;
        }),
        switchMap((data) => iif(() => data.diff !== undefined, of(data).pipe(
          map(data => {
            this.structureService.difference = data.diff;
            return data.diff;
          })
        ), of(data).pipe(
          map(data => {
            this.structureService.DiffMode = false;
            this.structureService.difference = [];
            return data;
          })
        )))
      )),
      takeUntil(this.structureService.done)
    ).subscribe();
    this.messageData = this.structureId.pipe(
      switchMap(structureId => this.structureService.getStructureRepeating(structureId).pipe(
        switchMap((data) => of(new MatTableDataSource<structure | structureWithChanges>([data]))),
        takeUntil(this.structureService.done)
      ))
    );
  }
  ngOnDestroy(): void {
    this.structureService.toggleDone = true;
  }
}
