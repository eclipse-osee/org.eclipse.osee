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
import {  Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { user } from '../../types/user-data-user';
import { UserDataAccountService } from '../../services/user-data-account.service';
import { animate, state, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'osee-display-user',
  templateUrl: './display-user.component.html',
  styleUrls: ['./display-user.component.sass'],
  animations: [
    trigger('expandButton', [
      state('closed', style({ transform: 'rotate(0)' })),
      state('open', style({ transform: 'rotate(-180deg)' })),
      transition('open => closed', animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
      transition('closed => open', animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
    ])
  ]
})
export class DisplayUserComponent implements OnInit {
  userInfo: Observable<user> = this.accountService.user;
  opened: boolean = false;
  
  constructor (private accountService: UserDataAccountService) {}

  ngOnInit(): void {
  }

}