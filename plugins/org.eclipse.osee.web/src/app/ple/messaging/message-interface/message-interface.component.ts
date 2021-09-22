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
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpLoadingService } from '../shared/services/ui/http-loading.service';
import { CurrentMessagesService } from './services/current-messages.service';

@Component({
  selector: 'app-message-interface',
  templateUrl: './message-interface.component.html',
  styleUrls: ['./message-interface.component.sass']
})
export class MessageInterfaceComponent implements OnInit {

  isLoading = this.loadingService.isLoading;
  constructor(private route: ActivatedRoute, private messageService: CurrentMessagesService, private loadingService: HttpLoadingService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((values) => {
      this.messageService.filter = values.get('type')?.trim().toLowerCase() || '';
      this.messageService.branch = values.get('branchId') || '';
      this.messageService.connection = values.get('connection') || '';
    })
  }

}
