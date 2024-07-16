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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import {
	MimRouteService,
	SharedConnectionUIService,
	SharedStructureUIService,
} from '@osee/messaging/shared/services';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { combineLatest, iif, of } from 'rxjs';
import { debounceTime, switchMap } from 'rxjs/operators';

@Component({
	selector: 'osee-messaging-header',
	templateUrl: './mim-header.component.html',
	styles: [],
	standalone: true,
	imports: [MatAnchor, RouterLink, NgIf, NgFor, AsyncPipe],
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
		debounceTime(0),
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
						this._currentBranchService.currentBranch.pipe(
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
																				'connections/' +
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
																				'connections/' +
																				type +
																				'/' +
																				id +
																				'/' +
																				connection +
																				'/messages/' +
																				message +
																				'/' +
																				submessage +
																				'/elements',
																		},
																		{
																			displayName:
																				structure.name,
																			routerLink:
																				'/ple/messaging/' +
																				'connections/' +
																				type +
																				'/' +
																				id +
																				'/' +
																				connection +
																				'/messages/' +
																				message +
																				'/' +
																				submessage +
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
																	'connections/' +
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
																	'connections/' +
																	type +
																	'/' +
																	id +
																	'/' +
																	connection +
																	'/messages/' +
																	message +
																	'/' +
																	submessage +
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
															'connections/' +
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

export default MimHeaderComponent;
