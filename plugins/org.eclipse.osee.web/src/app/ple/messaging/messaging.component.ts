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
import { tap } from 'rxjs/operators';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';

@Component({
  selector: 'app-messaging',
  templateUrl: './messaging.component.html',
  styleUrls: ['./messaging.component.sass']
})
export class MessagingComponent implements OnInit {

  constructor(private userService: UserDataAccountService) { }

  ngOnInit(): void {
  }

  userIsAdmin = this.userService.userIsAdmin;

}
