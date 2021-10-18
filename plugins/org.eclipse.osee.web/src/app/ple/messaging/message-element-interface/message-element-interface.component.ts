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
import { ActivatedRoute } from '@angular/router';
import { CurrentStateService } from './services/current-state.service';
import { structure } from './types/structure';
import { switchMap, takeUntil } from 'rxjs/operators';
import { of } from 'rxjs';
import { HttpLoadingService } from '../shared/services/ui/http-loading.service';


@Component({
  selector: 'ple-messaging-message-element-interface',
  templateUrl: './message-element-interface.component.html',
  styleUrls: ['./message-element-interface.component.sass'],
})
export class MessageElementInterfaceComponent implements OnInit, OnDestroy {
  isLoading = this.loadingService.isLoading;
  messageData = this.structureService.structures.pipe(
    switchMap((data)=>of(new MatTableDataSource<structure>(data))),
    takeUntil(this.structureService.done)
  );
  breadCrumb: string = '';
  constructor (
    private route: ActivatedRoute,
    public dialog: MatDialog,
    private structureService: CurrentStateService,
    private loadingService: HttpLoadingService,
  ) {}
  ngOnDestroy(): void {
    this.structureService.toggleDone = true;
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((values) => {
      this.breadCrumb = values.get('name') || '';
      this.structureService.BranchType = values.get('branchType') || '';
      this.structureService.branchId = values.get('branchId') || '';
      this.structureService.messageId = values.get('messageId') || '';
      this.structureService.subMessageId = values.get('subMessageId') || '';
      this.structureService.connection = values.get('connection') || '';
    });
  }
}
