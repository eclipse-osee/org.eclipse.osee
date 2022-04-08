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
import { Component, OnInit } from '@angular/core';
import { from } from 'rxjs';
import { filter, reduce, switchMap } from 'rxjs/operators';
import { DiffReportService } from '../../shared/services/ui/diff-report.service';
import { connectionDiffItem, DiffHeaderType } from '../../shared/types/DifferenceReport.d';

@Component({
  selector: 'app-connection-diffs',
  templateUrl: './connection-diffs.component.html',
  styleUrls: ['./connection-diffs.component.sass']
})
export class ConnectionDiffsComponent implements OnInit {

  constructor(private diffReportService: DiffReportService) { }

  ngOnInit(): void {
  }

  headers:(keyof connectionDiffItem)[] = [
    'name',
    'description',
    'transportType',
    'applicability'
  ]

  headerType = DiffHeaderType.CONNECTION;

  allConnections = this.diffReportService.connections;

  connectionsChanged = this.allConnections.pipe(
    switchMap(connections => from(connections).pipe(
      filter(connection => !connection.diffInfo?.added && !connection.diffInfo?.deleted),
      reduce((acc, curr) => [...acc, curr], [] as connectionDiffItem[])
    ))
  )

  connectionsAdded = this.allConnections.pipe(
    switchMap(connections => from(connections).pipe(
      filter(connection => connection.diffInfo?.added === true),
      reduce((acc, curr) => [...acc, curr], [] as connectionDiffItem[])
    ))
  )

  connectionsDeleted = this.allConnections.pipe(
    switchMap(connections => from(connections).pipe(
      filter(connection => connection.diffInfo?.deleted === true),
      reduce((acc, curr) => [...acc, curr], [] as connectionDiffItem[])
    ))
  )

}
