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
import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";

@Injectable()
export class ChartsService {
  constructor(private http: HttpClient) {}

  getBurnDownData(projectGuid: any) {
    return this.http.post(
      "service?url=/getproject/Charts/getEffortBurnDownInfo",
      projectGuid
    );
  }

  getStoryPointsBurnDownData(projectGuid: any) {
    return this.http.post(
      "service?url=/getproject/Charts/getStoryPointsBurndown",
      projectGuid
    );
  }
}
