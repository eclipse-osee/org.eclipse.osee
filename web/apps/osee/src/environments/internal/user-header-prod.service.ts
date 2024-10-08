/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserHeaderService } from '../user-header.service';

@Injectable({
	providedIn: 'root',
})
export class UserHeaderProdService extends UserHeaderService {
	useCustomHeaders = false;
	headers: HttpHeaders = new HttpHeaders();

	constructor() {
		super();
	}
}
