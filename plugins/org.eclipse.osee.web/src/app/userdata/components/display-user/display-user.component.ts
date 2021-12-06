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
import { Component, Inject,  OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { user } from '../../types/user-data-user';
import { UserDataAccountService } from '../../services/user-data-account.service';
import { UserDataCurrentUserService } from '../../services/user-data-current-user.service';
import { UserDataUIStateService } from '../../services/user-data-uistate.service';

@Component({
  selector: 'osee-display-user',
  templateUrl: './display-user.component.html',
  styleUrls: ['./display-user.component.sass']
})
export class DisplayUserComponent implements OnInit {
  userInfo: Observable<user> = this.accountService.user;
  
  constructor(private accountService: UserDataAccountService) { }

  ngOnInit(): void {
  }
}