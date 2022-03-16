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
import { filter, map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { combineLatest, iif, of } from 'rxjs';


@Component({
  selector: 'ple-messaging-message-element-interface',
  templateUrl: './message-element-interface.component.html',
  styleUrls: ['./message-element-interface.component.sass'],
})
export class MessageElementInterfaceComponent implements OnInit, OnDestroy {
  messageData = this.structureService.structures.pipe(
    switchMap((data)=>of(new MatTableDataSource<structure>(data))),
    takeUntil(this.structureService.done)
  );
  breadCrumb: string = '';
  constructor (
    private route: ActivatedRoute,
    private router:Router,
    public dialog: MatDialog,
    private structureService: CurrentStructureService,
  ) {
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      map(() => this.route),
      switchMap(route => {
        while (route.firstChild && route.firstChild.snapshot.paramMap.keys.length!==0) {
          route = route.firstChild
        }
        return of(route);
      }),
      filter((activatedRoute) => activatedRoute.outlet === 'primary'),
      switchMap((route) => combineLatest([route.paramMap, route.firstChild?.data || route.url]).pipe(
        map(([paramMap, data]) => {
          this.structureService.BranchType = paramMap.get('branchType') || '';
          this.structureService.branchId = paramMap.get('branchId') || '';
          this.structureService.messageId = paramMap.get('messageId') || '';
          this.structureService.subMessageId = paramMap.get('subMessageId') || '';
          this.structureService.connection = paramMap.get('connection') || '';
          this.breadCrumb = paramMap.get('name') || '';
          return data;
        }),
        switchMap((data) => iif(() => data?.diff !== undefined, of(data).pipe(
          map(data => {
            this.structureService.difference = data?.diff;
            return data?.diff;
          })
        ), of(data).pipe(
          map(data => {
            this.structureService.DiffMode = false;
            this.structureService.difference = [];
            return data;
          })
        )))
      ))
    ).subscribe((val) => {
    });

  }
  ngOnDestroy(): void {
    this.structureService.toggleDone = true;
  }

  ngOnInit(): void {
  }
}
