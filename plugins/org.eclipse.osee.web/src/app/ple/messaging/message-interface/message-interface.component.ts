import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpLoadingService } from '../shared/services/ui/http-loading.service';
import { CurrentMessagesService } from './services/current-messages.service';

@Component({
  selector: 'app-message-interface',
  templateUrl: './message-interface.component.html',
  styleUrls: ['./message-interface.component.sass']
})
export class MessageInterfaceComponent implements OnInit {

  isLoading = this.loadingService.isLoading;
  constructor(private route: ActivatedRoute, private messageService: CurrentMessagesService, private loadingService: HttpLoadingService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((values) => {
      this.messageService.filter = values.get('type')?.trim().toLowerCase() || '';
      this.messageService.branch = values.get('branchId') || '';
    })
  }

}
