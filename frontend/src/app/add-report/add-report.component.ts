import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle} from '@angular/material';
import {AnalyticsService} from '../analytics.service';

@Component({
  selector: 'app-add-report',
  providers: [AnalyticsService],
  templateUrl: './add-report.component.html',
  styleUrls: ['./add-report.component.css']
})
export class AddReportComponent  implements OnInit {
  public name:string='';
  public query:string = '';
  public createdBy:string = '';
  public dbTypes = [];
  public title = "Add Report";
  public reportId:number = null;
  public selectedDB:number;

  constructor(public dialogRef: MatDialogRef<AddReportComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private analyticsService:AnalyticsService) {
        this.analyticsService.getDatabases().subscribe(data=>{
            this.dbTypes = data.json();
            this.selectedDB = this.dbTypes[0].dsId;
        });
   
     }

  ngOnInit(){
    if(this.data && this.data.reportId) {
      this.reportId = this.data.reportId;
      this.analyticsService.getReport(this.data.reportId)
      .subscribe(data=>{
             var report = data.json();
             this.name = report.name;
             this.title = "Edit Report "+report.name;
             this.query = report.query;
             this.selectedDB =  report.dataSource.dsId; 
             this.createdBy = report.createdBy;
      });  

    }
  }   

  onNoClick(): void {
    this.dialogRef.close(false);
  }

  addReport():void{
      this.analyticsService.
          addReport(this.name, this.query, this.selectedDB, this.createdBy)
          .subscribe(data=>{
            this.dialogRef.close(true);
          });  
  }

  updateReport():void{
    this.analyticsService.updateReport( this.reportId, this.name, this.query, this.selectedDB, this.createdBy)
        .subscribe(data=>{
          this.dialogRef.close(true);
        });  
  }

}
