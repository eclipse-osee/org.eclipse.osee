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
import { Injectable, inject } from '@angular/core';
import { combineLatest, filter, repeat, switchMap, take, tap } from 'rxjs';
import { DashboardHttpService } from './dashboard-http.service';
import { CiDashboardUiService } from './ci-dashboard-ui.service';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { NamedId } from '@osee/shared/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { TransactionService } from '@osee/transactions/services';

@Injectable({
	providedIn: 'root',
})
export class DashboardService {
	private uiService = inject(CiDashboardUiService);
	private dashboardHttpService = inject(DashboardHttpService);
	private transactionService = inject(TransactionService);

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
		pageNum: string | number,
		pageSize: number,
		orderByAttributeId: string
	) {
		return this.uiService.branchId.pipe(
			filter((branchId) => branchId !== '' && branchId !== '-1'),
			switchMap((branchId) =>
				this.dashboardHttpService
					.getSubsystems(
						branchId,
						filterText,
						pageNum,
						pageSize,
						orderByAttributeId
					)
					.pipe(
						repeat({ delay: () => this.uiService.updateRequired })
					)
			)
		);
	}

	getSubsystemsCount(filterText: string) {
		return this.uiService.branchId.pipe(
			filter((branchId) => branchId !== '' && branchId !== '-1'),
			switchMap((branchId) =>
				this.dashboardHttpService
					.getSubsystemsCount(branchId, filterText)
					.pipe(
						repeat({ delay: () => this.uiService.updateRequired })
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
		pageNum: string | number,
		pageSize: number,
		orderByAttributeId: string
	) {
		return this.uiService.branchId.pipe(
			filter((branchId) => branchId !== '' && branchId !== '-1'),
			switchMap((branchId) =>
				this.dashboardHttpService
					.getTeams(
						branchId,
						filterText,
						pageNum,
						pageSize,
						orderByAttributeId
					)
					.pipe(
						repeat({ delay: () => this.uiService.updateRequired })
					)
			)
		);
	}

	getTeamsCount(filterText: string) {
		return this.uiService.branchId.pipe(
			filter((branchId) => branchId !== '' && branchId !== '-1'),
			switchMap((branchId) =>
				this.dashboardHttpService
					.getTeamsCount(branchId, filterText)
					.pipe(
						repeat({ delay: () => this.uiService.updateRequired })
					)
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

	updateAttribute(
		artId: `${number}`,
		attributeTypeId: `${number}`,
		value: string
	) {
		return this.uiService.branchId.pipe(
			take(1),
			switchMap((branchId) =>
				this.transactionService.performMutation({
					branch: branchId,
					txComment: `Modifying ${artId}`,
					modifyArtifacts: [
						{
							id: artId,
							setAttributes: [
								{
									typeId: attributeTypeId,
									value: value,
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
