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
import { combineLatest, iif, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CurrentBranchInfoService } from '../../../../../ple-services/httpui/current-branch-info.service';
import { MimRouteService } from '../../services/ui/mim-route.service';
import { SharedConnectionUIService } from '../../services/ui/shared-connection-ui.service';
import { SharedStructureUIService } from '../../services/ui/shared-structure-ui.service';

@Component({
	selector: 'osee-messaging-header',
	templateUrl: './mim-header.component.html',
	styleUrls: ['./mim-header.component.sass'],
})
export class MimHeaderComponent {
	route = combineLatest([
		this._routeService.type,
		this._routeService.id,
		this._routeService.connectionId,
		this._routeService.messageId,
		this._routeService.submessageId,
		this._routeService.submessageToStructureBreadCrumbs,
		this._routeService.singleStructureId,
	]).pipe(
		switchMap(
			([
				type,
				id,
				connection,
				message,
				submessage,
				submessageToStructureBreadCrumbs,
				singleStructureId,
			]) =>
				iif(
					() => type !== '',
					iif(
						() => id != '',
						this._currentBranchService.currentBranchDetail.pipe(
							switchMap((detail) =>
								iif(
									() =>
										connection !== '0' && connection !== '',
									this._connectionService.connection.pipe(
										switchMap((connectionDetails) =>
											iif(
												() => message !== '',
												iif(
													() => submessage !== '',
													iif(
														() =>
															singleStructureId !==
															'',
														this._structureService.structure.pipe(
															switchMap(
																(structure) =>
																	of([
																		{
																			displayName:
																				type,
																			routerLink:
																				'/ple/messaging/' +
																				'connections' +
																				'/' +
																				type,
																		},
																		{
																			displayName:
																				detail.name,
																			routerLink:
																				'/ple/messaging/' +
																				'connections' +
																				'/' +
																				type +
																				'/' +
																				id +
																				'/',
																		},
																		{
																			displayName:
																				connectionDetails.name,
																			routerLink:
																				'/ple/messaging/' +
																				type +
																				'/' +
																				id +
																				'/' +
																				connection +
																				'/messages',
																		},
																		{
																			displayName:
																				submessageToStructureBreadCrumbs,
																			routerLink:
																				'/ple/messaging/' +
																				type +
																				'/' +
																				id +
																				'/' +
																				connection +
																				'/messages/' +
																				message +
																				'/' +
																				submessage +
																				'/' +
																				submessageToStructureBreadCrumbs +
																				'/elements',
																		},
																		{
																			displayName:
																				structure.name,
																			routerLink:
																				'/ple/messaging/' +
																				type +
																				'/' +
																				id +
																				'/' +
																				connection +
																				'/messages/' +
																				message +
																				'/' +
																				submessage +
																				'/' +
																				submessageToStructureBreadCrumbs +
																				'/elements/' +
																				singleStructureId,
																		},
																	])
															)
														), //submessage,
														of([
															{
																displayName:
																	type,
																routerLink:
																	'/ple/messaging/' +
																	'connections' +
																	'/' +
																	type,
															},
															{
																displayName:
																	detail.name,
																routerLink:
																	'/ple/messaging/' +
																	'connections' +
																	'/' +
																	type +
																	'/' +
																	id +
																	'/',
															},
															{
																displayName:
																	connectionDetails.name,
																routerLink:
																	'/ple/messaging/' +
																	type +
																	'/' +
																	id +
																	'/' +
																	connection +
																	'/messages',
															},
															{
																displayName:
																	submessageToStructureBreadCrumbs,
																routerLink:
																	'/ple/messaging/' +
																	type +
																	'/' +
																	id +
																	'/' +
																	connection +
																	'/messages/' +
																	message +
																	'/' +
																	submessage +
																	'/' +
																	submessageToStructureBreadCrumbs +
																	'/elements',
															},
														]) //submessage)
													),
													of() //message, DNE
												),
												of([
													{
														displayName: type,
														routerLink:
															'/ple/messaging/' +
															'connections' +
															'/' +
															type,
													},
													{
														displayName:
															detail.name,
														routerLink:
															'/ple/messaging/' +
															'connections' +
															'/' +
															type +
															'/' +
															id +
															'/',
													},
													{
														displayName:
															connectionDetails.name,
														routerLink:
															'/ple/messaging/' +
															type +
															'/' +
															id +
															'/' +
															connection +
															'/messages',
													},
												])
											)
										)
									),
									of([
										{
											displayName: type,
											routerLink:
												'/ple/messaging/' +
												'connections' +
												'/' +
												type,
										},
										{
											displayName: detail.name,
											routerLink: type + '/' + id,
										},
									])
								)
							)
						),
						of([{ displayName: type, routerLink: type }])
					),
					of()
				)
		)
	);

	constructor(
		private _routeService: MimRouteService,
		private _currentBranchService: CurrentBranchInfoService,
		private _connectionService: SharedConnectionUIService,
		private _structureService: SharedStructureUIService
	) {}
}
