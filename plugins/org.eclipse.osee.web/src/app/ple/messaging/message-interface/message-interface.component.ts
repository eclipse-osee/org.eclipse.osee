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
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, iif, of } from 'rxjs';
import { HttpLoadingService } from '../shared/services/ui/http-loading.service';
import { CurrentMessagesService } from './services/current-messages.service';

@Component({
  selector: 'app-message-interface',
  templateUrl: './message-interface.component.html',
  styleUrls: ['./message-interface.component.sass']
})
export class MessageInterfaceComponent implements OnInit,OnDestroy {

  isLoading = this.loadingService.isLoading;
  constructor (private router:Router,private route: ActivatedRoute, private messageService: CurrentMessagesService, private loadingService: HttpLoadingService) { }
  ngOnDestroy(): void {
    this.messageService.toggleDone=true;
  }

  ngOnInit(): void {
    combineLatest([this.route.paramMap,this.route.data,iif(() => this.router.url.includes('diff'),
    of(false),
      of(true))]).subscribe(([values, data, mode]) => {
        if (mode) {
          this.messageService.filter = values.get('type')?.trim().toLowerCase() || '';
          this.messageService.branch = values.get('branchId') || '';
          this.messageService.connection = values.get('connection') || '';
          this.messageService.DiffMode = false;
        } else {
          this.messageService.connection = values.get('connection') || '';
          this.messageService.difference=data.diff;
        }
    })
  }

}
