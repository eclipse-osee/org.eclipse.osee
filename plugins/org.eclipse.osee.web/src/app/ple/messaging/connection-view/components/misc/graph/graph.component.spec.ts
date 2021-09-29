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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Edge, NgxGraphModule, Node } from '@swimlane/ngx-graph';
import { graphServiceMock } from '../../../mocks/CurrentGraphService.mock';
import { ConnectionViewRouterService } from '../../../services/connection-view-router.service';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';


import { GraphComponent } from './graph.component';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatMenuHarness, MatMenuItemHarness } from '@angular/material/menu/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { ConfirmRemovalDialogComponent } from '../../dialogs/confirm-removal-dialog/confirm-removal-dialog.component';
import { CreateConnectionDialogComponent } from '../../dialogs/create-connection-dialog/create-connection-dialog.component';
import { CreateNewNodeDialogComponent } from '../../dialogs/create-new-node-dialog/create-new-node-dialog.component';
import { EditConnectionDialogComponent } from '../../dialogs/edit-connection-dialog/edit-connection-dialog.component';
import { EditNodeDialogComponent } from '../../dialogs/edit-node-dialog/edit-node-dialog.component';
import { EnumsService } from 'src/app/ple/messaging/shared/services/http/enums.service';
import { enumsServiceMock } from 'src/app/ple/messaging/shared/mocks/EnumsService.mock';
import { of } from 'rxjs';
import { connection, transportType } from 'src/app/ple/messaging/shared/types/connection';
import { node } from 'src/app/ple/messaging/shared/types/node';

describe('GraphComponent', () => {
  let component: GraphComponent;
  let fixture: ComponentFixture<GraphComponent>;
  let loader: HarnessLoader;
  let router: any;
  let routerService: ConnectionViewRouterService;
  let menuLinkHarness: MatMenuHarness;
  let menuNodeHarness: MatMenuHarness;
  let menuGraphHarness: MatMenuHarness;

  beforeEach(async () => {
    router = jasmine.createSpyObj('Router', ['navigate', 'createUrlTree', 'serializeUrl'],{'url':new String()});
    await TestBed.configureTestingModule({
      imports:[MatDialogModule,NgxGraphModule,NoopAnimationsModule,RouterTestingModule,MatMenuModule,MatFormFieldModule,FormsModule,MatSelectModule,MatInputModule,CommonModule],
      providers:
        [
          { provide: Router, useValue: router },
          { provide: CurrentGraphService, useValue: graphServiceMock },
          {provide: EnumsService,useValue:enumsServiceMock}
      ],
      declarations: [ GraphComponent,ConfirmRemovalDialogComponent,CreateConnectionDialogComponent,CreateNewNodeDialogComponent,EditConnectionDialogComponent,EditNodeDialogComponent ]
    })
      .compileComponents();
    routerService=TestBed.inject(ConnectionViewRouterService)
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });
  beforeEach(function () {
    let window1 = spyOn(window, 'open').and.callFake((url,target,replace) => {
      return null;
    })
  });
  beforeEach(() => {
    component.linkMenuTrigger.closeMenu();
    component.nodeMenuTrigger.closeMenu();
  })

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Core Functionality', () => {
    
    describe('View Functionality', () => {
      beforeEach(() => {
        component.editMode = false;
        fixture.detectChanges();
      })

      it('should navigate to messages page', () => {
        routerService.branchType = 'product line';
        routerService.branchId = '8';
        component.navigateToMessages("hello");
        expect(router.navigate).toHaveBeenCalledWith(['', 'product line', '8', 'hello', 'messages'])
      });
    })
      describe('Editing Menus', () => {
        beforeEach(() => {
          component.editMode = true;
          fixture.detectChanges();
        })
        describe('Link Menu', () => {
          beforeEach(async () => {
            menuLinkHarness = await loader.getHarness(MatMenuHarness.with({ triggerText: 'LinkMenu' }));
            component.linkMenuTrigger.menuData = {
              data: {
                id: "1",
                name: "edge",
                applicability: {
                  id: "1",
                  name: "Base"
                },
                dashed: false,
                description: '',
                transportType:transportType.Ethernet
              },
              source: {
                id : "2",
                label : "Node 1",
                data : {
                  id : "2",
                  name : "Node 1",
                  applicability : {
                    id : "1",
                    name : "Base"
                  },
                  interfaceNodeBgColor : "",
                  interfaceNodeAddress : "",
                  description : ""
                }
              },
              target:{
                id : "3",
                label : "Node 2",
                data : {
                  id : "3",
                  name : "Node 2",
                  applicability : {
                    id : "1",
                    name : "Base"
                  },
                  interfaceNodeBgColor : "",
                  interfaceNodeAddress : "",
                  description : ""
                }
              }
            }
          })
          it('menu harness should be defined', () => {
            expect(menuLinkHarness).toBeDefined();
          })
          describe('Testing open function', () => {
            it('should open the menu with proper initialization and close/reset', async() => {
              component.openLinkDialog(new MouseEvent("contextmenu", { clientX: 100, clientY: 100 }), { id: '1', source: '10', target: '15', data: { name: '1' } }, [{ id: '10', label: '10' }, { id: '15', label: '15' }]);
              expect(await menuLinkHarness.isOpen()).toBeTrue();
              component.linkMenuTrigger.menuData = {
                data: {
                  "id": "1",
                  "name": "edge",
                  "applicability": {
                    "id": "1",
                    "name": "Base"
                  }
                },
                source: {
                  "id" : "2",
                  "label" : "Node 1",
                  "data" : {
                    "id" : "2",
                    "name" : "Node 1",
                    "applicability" : {
                      "id" : "1",
                      "name" : "Base"
                    },
                    "interfaceNodeBgColor" : "",
                    "interfaceNodeAddress" : "",
                    "description" : ""
                  }
                },
                target:{
                  "id" : "3",
                  "label" : "Node 2",
                  "data" : {
                    "id" : "3",
                    "name" : "Node 2",
                    "applicability" : {
                      "id" : "1",
                      "name" : "Base"
                    },
                    "interfaceNodeBgColor" : "",
                    "interfaceNodeAddress" : "",
                    "description" : ""
                  }
                }
              }
              menuLinkHarness.close();
            })
          })
          describe('Open Menu Tests', () => {
            let items : MatMenuItemHarness[]=[];
            beforeEach(async () => {
              await menuLinkHarness.open();
              items = await menuLinkHarness.getItems();
            })
            it('should have the correct amount of items(4)', () => {
              expect(items.length).toEqual(4)
            })
            it('should be able to navigate to a url', async() => {
              let spy = spyOn(component, 'navigateToMessages').and.callThrough();
              await menuLinkHarness.clickItem({ text: 'Go to edge' });
              expect(spy).toHaveBeenCalledWith('1')
            })
            it('should be able to navigate to a url in a new tab', async () => {
              let spy = spyOn(component, 'navigateToMessagesInNewTab').and.callThrough();
              await menuLinkHarness.clickItem({ text: 'Go to edge in new tab' });
              expect(spy).toHaveBeenCalledWith('1')
            })
            it('should open the connection edit dialog', async () => {
              let connection = {
                id: "1",
                name: "edge",
                applicability: {
                  id: "1",
                  name: "Base"
                },
                dashed: false,
                description: '',
                transportType:transportType.Ethernet
              }
              let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(connection), close: null });
              let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
              let spy = spyOn(component, 'openConnectionEditDialog').and.callThrough();
              await menuLinkHarness.clickItem({ text: 'Edit edge' });
              expect(spy).toHaveBeenCalledWith(connection);
            })

            it('should open the remove connection dialog', async () => {
              let connection = {
                id: "1",
                name: "edge",
                applicability: {
                  id: "1",
                  name: "Base"
                },
                dashed: false,
                description: '',
                transportType:transportType.Ethernet
              }
              let source = {
                id: "2",
                label: "Node 1",
                data: {
                  id: "2",
                  name: "Node 1",
                  applicability: {
                    id: "1",
                    name: "Base"
                  },
                  interfaceNodeBgColor: "",
                  interfaceNodeAddress: "",
                  description: ""
                }
              }
              let target ={
                id : "3",
                label : "Node 2",
                data : {
                  id : "3",
                  name : "Node 2",
                  applicability : {
                    id : "1",
                    name : "Base"
                  },
                  interfaceNodeBgColor : "",
                  interfaceNodeAddress : "",
                  description : ""
                }
              }
              let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of({id:connection.id,name:connection.name,extraNames:[source.label,target.label],type:'connection'}), close: null });
              let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
              let spy = spyOn(component, 'openRemoveConnectionDialog').and.callThrough();
              await menuLinkHarness.clickItem({ text: 'Remove connection edge' });
              expect(spy).toHaveBeenCalledWith(connection,source,target);
            })
            afterEach(async () => {
              if (await menuLinkHarness.isOpen()) {
                await menuLinkHarness.close();
              } else {
                await 0;
              }
            })
          })
        })
        describe('Node Menu', () => {
          let node:Node={
            id : "2",
            label : "Node 1",
            data : {
              id : "2",
              name : "Node 1",
              applicability : {
                id : "1",
                name : "Base"
              },
              interfaceNodeBgColor : "",
              interfaceNodeAddress : "",
              description : ""
            }
          }
          let connections:Edge[]=[{source:'2',target:'5'},{target:'2',source:'7'}]
          beforeEach(async() => {
            menuNodeHarness = await loader.getHarness(MatMenuHarness.with({ triggerText: 'NodeMenu' }));
            component.nodeMenuTrigger.menuData = {
              data: node.data,
              sources: [connections[0]],
              targets:[connections[1]]
            }
          })
          it('menu harness should be defined', () => {
            expect(menuNodeHarness).toBeDefined();
          })
          describe('Testing open function', () => {
            it('should open the menu with proper initialization and close/reset', async() => {
              component.openNodeDialog(new MouseEvent("contextmenu", { clientX: 200, clientY: 200 }), {id:'1',data:{name:'first'}},[{id:'a3',source:'1',target:'2',data:{name:'a'}},{id:'a4',source:'2',target:'1',data:{name:'b'}}]);
              expect(await menuNodeHarness.isOpen()).toBeTrue();
              component.nodeMenuTrigger.menuData={
                data: node.data,
                sources: [connections[0]],
                targets:[connections[1]]
              }
              menuNodeHarness.close();
            })
          })
          describe('Open Menu Tests', () => {
            let items : MatMenuItemHarness[]=[];
            beforeEach(async () => {
              await menuNodeHarness.open();
              items = await menuNodeHarness.getItems();
            })
            it('should have the correct amount of items(3)', () => {
              expect(items.length).toEqual(3)
            })
            it('should open the edit node dialog', async() => {
              let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(node.data), close: null });
              let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
              let spy = spyOn(component, 'openEditNodeDialog').and.callThrough();
              await menuNodeHarness.clickItem({ text: 'Edit Node 1' });
              expect(spy).toHaveBeenCalledWith(node.data);
            })

            it('should open the remove node and connection dialog', async () => {
              let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of({id:node.data.id,name:node.data.name,extraNames:[connections[0].label,connections[1].label],type:'node'}), close: null });
              let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
              let spy = spyOn(component, 'removeNodeAndConnection').and.callThrough();
              await menuNodeHarness.clickItem({ text: 'Remove Node 1 & Connection' });
              expect(spy).toHaveBeenCalledWith(node.data,[connections[0]],[connections[1]]);
            })

            it('should open the create connection to dialog', async () => {
              let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of({data:node.data}), close: null });
              let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
              let spy = spyOn(component, 'createConnectionToNode').and.callThrough();
              await menuNodeHarness.clickItem({ text: 'Create Connection To Node 1' });
              expect(spy).toHaveBeenCalledWith(node.data);
            })
          })
          afterEach(async () => {
            if (await menuNodeHarness.isOpen()) {
              await menuNodeHarness.close();
            } else {
              await 0;
            }
          })
        })

        describe('Graph Menu', () => {
          beforeEach(async () => {
            menuGraphHarness = await loader.getHarness(MatMenuHarness.with({ triggerText: 'GraphMenu' }));
          })
          it('menu harness should be defined', () => {
            expect(menuGraphHarness).toBeDefined();
          })
          describe('Testing open function', () => {
            it('should open the menu with proper initialization and close/reset', async () => {
              let event = new MouseEvent("contextmenu", { clientX: 300, clientY: 300, },);
              let el = document.createElement('button');
              el.classList.add('panning-rect')
              Object.defineProperty(event, 'target', {value: el, enumerable: true});
              component.openGraphDialog(event);
              expect(await menuGraphHarness.isOpen()).toBeTrue();
              menuGraphHarness.close();
            })
          })
          describe('Open Menu Tests', () => {
            let items : MatMenuItemHarness[]=[];
            beforeEach(async () => {
              await menuGraphHarness.open();
              items = await menuGraphHarness.getItems();
            })
            it('should have the correct amount of items(1)', () => {
              expect(items.length).toEqual(1)
            })
            it('should open create new node dialog',async () => {
              let response:node={name:'abcdef',description:'jkl',applicability:{id:'1',name:'Base'}}
              let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(response), close: null });
              let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
              let spy = spyOn(component, 'createNewNode').and.callThrough();
              await menuGraphHarness.clickItem({ text: 'Create New Node' });
              expect(spy).toHaveBeenCalled();
            })
          })
          afterEach(async() => {
            if (await menuGraphHarness.isOpen()) {
              menuGraphHarness.close();
            } else {
              await 0;
            }
          })
        })
      })
    })
    
});
