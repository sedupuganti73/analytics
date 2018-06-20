import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle} from '@angular/material';
import {AnalyticsService} from '../analytics.service';
import { catchError } from 'rxjs/operators/catchError';


@Component({
  selector: 'app-add-report',
  providers: [AnalyticsService],
  templateUrl: './add-report.component.html',
  styleUrls: ['./add-report.component.css']
})
export class AddReportComponent  implements OnInit {
  public name = '';
  public query = '';
  public createdBy = '';
  public dbTypes = [];
  public title = 'Add Report';
  public reportId: number = null;
  public selectedDB: number;
  public loading = false;
  public errorMessage = '';
  public loadType ='';
  public priority='';
  public loadTypes = ['Once','Daily','Hourly'];
  public runTime='';
  public recordCountQuery ='';
  

  constructor(public dialogRef: MatDialogRef<AddReportComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private analyticsService: AnalyticsService
  ) {
        this.analyticsService.getDatabases().subscribe( data => {
            this.dbTypes = data;
            this.selectedDB = this.dbTypes[0].dsId;
        });
     }

  ngOnInit() {
    if ( this.data && this.data.reportId) {
      this.reportId = this.data.reportId;
      this.analyticsService.getReport(this.data.reportId)
      .subscribe(data => {
             const report = data;
             this.name = report.name;
             this.title = 'Edit Report ' + report.name;
             this.query = report.query;
             this.selectedDB =  report.dataSource.dsId;
             this.createdBy = report.createdBy;
             if (report.loadType != null) {
                 if (report.loadType == 0) {
                    this.loadType = 'Once';
                 } else if (report.loadType == 1) {
                    this.loadType = 'Daily';
                 } else if (report.loadType == 1) {
                     this.loadType = 'Hourly';
                 }
             }
             this.priority = report.priority;
             this.runTime = report.runTime;
             this.recordCountQuery = report.recordCountQuery;
       });

    }
  }

  onNoClick(): void {
    this.dialogRef.close(false);
  }

  addReport(): void {
      this.loading = true;
      this.errorMessage = '';
      this.analyticsService.
          addReport(this.name, this.query, this.selectedDB, this.createdBy,this.loadType,this.priority,this.runTime,this.recordCountQuery)
          .subscribe(data => {
            this.loading = false;
            this.dialogRef.close(true);
          },
          err => {
            console.log(err.error.message);
            this.errorMessage = err.error.message;
            this.loading = false;
          }
          )
          ;
  }

  updateReport(): void {
    this.analyticsService.updateReport( this.reportId, this.name, this.query, this.selectedDB, this.createdBy,this.loadType,this.priority,this.runTime,this.recordCountQuery)
        .subscribe(data => {
          this.dialogRef.close(true);
        });
  }

}
