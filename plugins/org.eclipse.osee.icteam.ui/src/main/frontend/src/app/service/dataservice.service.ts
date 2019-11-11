/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable()
export class DataserviceService {
  private messageSource = new Subject<any>();
  projectList = this.messageSource.asObservable();

  private releaseMessageSource = new Subject<any>();
  releaseList = this.releaseMessageSource.asObservable();

  public sprintMessageSource = new Subject<any>();
  public taskMessageSource = new Subject<any>();
  sprintMessageSource$ = this.sprintMessageSource.asObservable();
  ProjectData = [];
  releaseData = [];

  constructor() { }

  addProject(message: any) {
    this.messageSource.next(message);
  }

  addRelease(message: any) {
    this.releaseMessageSource.next(message);
  }

  sendRelease(message: any) {
    this.sprintMessageSource.next(message);
  }
}
