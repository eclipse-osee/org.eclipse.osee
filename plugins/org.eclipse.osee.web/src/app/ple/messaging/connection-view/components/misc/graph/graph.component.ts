import { Component, OnInit } from '@angular/core';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { Subject } from 'rxjs';
import { ConnectionViewRouterService } from '../../../services/connection-view-router.service';

@Component({
  selector: 'osee-connectionview-graph',
  templateUrl: './graph.component.html',
  styleUrls: ['./graph.component.sass']
})
export class GraphComponent implements OnInit {

  data = this.graphService.nodes;
  update = this.graphService.updated;
  constructor (private graphService: CurrentGraphService, private router: ConnectionViewRouterService) {}

  ngOnInit(): void {
    this.graphService.update = true;
  }

  navigateToMessages(value: string) {
    this.router.connection = value;
  }
}
