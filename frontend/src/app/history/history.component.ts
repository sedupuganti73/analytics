import { Component, OnInit } from '@angular/core';
import {DataSource} from '@angular/cdk/collections';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import {AnalyticsService} from '../analytics.service';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { ActivatedRoute, ParamMap } from '@angular/router';


@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css'],
  providers: [AnalyticsService]
})
export class HistoryComponent implements OnInit {
  public datasource: DataSource<ReportHistory> = new ReportHistoriesDataSource() ;
  public displayedColumns = [ 'startDateStr', 'endDateStr',  'recordCountBefore', 'recordCountAfter', 'message'];
  public loading = true;
  public reportId: number;
  constructor(private route: ActivatedRoute, private analyticsService: AnalyticsService, public dialog: MatDialog) { }

  ngOnInit() {
   this.reportId = +this.route.snapshot.paramMap.get('report-id');
    this.analyticsService.getHistory(this.reportId).subscribe(data => {
        histories = data;
        this.datasource = new ReportHistoriesDataSource();
        this.loading = false;
    });
  }

}

export interface ReportHistory {

  historyId: string;
  reportId: string;
  startDateStr: string;
  endDateStr: string;
  message: string;
  recordCountBefore:number;
  recordCountAfter:number;


}

let histories: ReportHistory[] = [];

export class ReportHistoriesDataSource extends DataSource<any> {
  /*constructor(private siteId:string, private adminService:AdminService){
    super();
  }*/

  /** Connect function called by the table to retrieve one stream containing the data to render. */
  connect(): Observable<ReportHistory[]> {
    return Observable.of(histories);
  }

  disconnect() {}
}
