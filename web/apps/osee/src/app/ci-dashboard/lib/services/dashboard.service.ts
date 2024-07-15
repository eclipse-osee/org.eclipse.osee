/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Injectable } from '@angular/core';
import { combineLatest, filter, switchMap, take, tap } from 'rxjs';
import { DashboardHttpService } from './dashboard-http.service';
import { CiDashboardUiService } from './ci-dashboard-ui.service';
import { TransactionService } from '@osee/shared/transactions';
import {
	ARTIFACTTYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
} from '@osee/shared/types/constants';
import { NamedId } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class DashboardService {
	constructor(
		private uiService: CiDashboardUiService,
		private dashboardHttpService: DashboardHttpService,
		private transactionService: TransactionService
	) {}

	private _teamStats = combineLatest([
		this.uiService.branchId,
		this.uiService.ciSetId,
	]).pipe(
		filter(([branchId, ciSetId]) => branchId !== '' && ciSetId !== ''),
		switchMap(([branchId, ciSetId]) =>
			this.dashboardHttpService.getTeamStats(branchId, ciSetId)
		)
	);

	private _subsystemStats = combineLatest([
		this.uiService.branchId,
		this.uiService.ciSetId,
	]).pipe(
		filter(([branchId, ciSetId]) => branchId !== '' && ciSetId !== ''),
		switchMap(([branchId, ciSetId]) =>
			this.dashboardHttpService.getSubsystemStats(branchId, ciSetId)
		)
	);

	private _timelineStats = combineLatest([
		this.uiService.branchId,
		this.uiService.ciSetId,
	]).pipe(
		filter(([branchId, ciSetId]) => branchId !== '' && ciSetId !== ''),
		switchMap(([branchId, ciSetId]) =>
			this.dashboardHttpService.getTimelineStats(branchId, ciSetId)
		)
	);

	getSubsystemsPaginated(
		filterText: string,
		pageNum: number,
		pageSize: number,
		orderByAttributeId: string
	) {
		return this.uiService.branchId.pipe(
			filter((branchId) => branchId !== '' && branchId !== '-1'),
			switchMap((branchId) =>
				this.dashboardHttpService.getSubsystems(
					branchId,
					filterText,
					pageNum,
					pageSize,
					orderByAttributeId
				)
			)
		);
	}

	getSubsystemsCount(filterText: string) {
		return this.uiService.branchId.pipe(
			filter((branchId) => branchId !== '' && branchId !== '-1'),
			switchMap((branchId) =>
				this.dashboardHttpService.getSubsystemsCount(
					branchId,
					filterText
				)
			)
		);
	}

	createSubsystem(name: string) {
		return this.uiService.branchId.pipe(
			take(1),
			switchMap((branchId) =>
				this.transactionService.performMutation({
					branch: branchId,
					txComment: `Create Script Subsystem ${name}`,
					createArtifacts: [
						{
							typeId: ARTIFACTTYPEIDENUM.SCRIPTSUBSYSTEM,
							name: name,
						},
					],
				})
			),
			tap((_) => (this.uiService.update = true))
		);
	}

	getTeamsPaginated(
		filterText: string,
		pageNum: number,
		pageSize: number,
		orderByAttributeId: string
	) {
		return this.uiService.branchId.pipe(
			filter((branchId) => branchId !== '' && branchId !== '-1'),
			switchMap((branchId) =>
				this.dashboardHttpService.getTeams(
					branchId,
					filterText,
					pageNum,
					pageSize,
					orderByAttributeId
				)
			)
		);
	}

	getTeamsCount(filterText: string) {
		return this.uiService.branchId.pipe(
			filter((branchId) => branchId !== '' && branchId !== '-1'),
			switchMap((branchId) =>
				this.dashboardHttpService.getTeamsCount(branchId, filterText)
			)
		);
	}

	createTeam(name: string) {
		return this.uiService.branchId.pipe(
			take(1),
			switchMap((branchId) =>
				this.transactionService.performMutation({
					branch: branchId,
					txComment: `Create Script Team ${name}`,
					createArtifacts: [
						{
							typeId: ARTIFACTTYPEIDENUM.SCRIPTTEAM,
							name: name,
						},
					],
				})
			),
			tap((_) => (this.uiService.update = true))
		);
	}

	updateArtifact(value: NamedId) {
		return this.uiService.branchId.pipe(
			take(1),
			switchMap((branchId) =>
				this.transactionService.performMutation({
					branch: branchId,
					txComment: `Modifying ${value.id}`,
					modifyArtifacts: [
						{
							id: value.id,
							setAttributes: [
								{
									typeId: ATTRIBUTETYPEIDENUM.NAME,
									value: value.name,
								},
							],
						},
					],
				})
			),
			tap((_) => (this.uiService.update = true))
		);
	}

	get teamStats() {
		return this._teamStats;
	}

	get subsystemStats() {
		return this._subsystemStats;
	}

	get timelineStats() {
		return this._timelineStats;
	}
}
