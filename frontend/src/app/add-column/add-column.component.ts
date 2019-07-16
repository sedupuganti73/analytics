import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle} from '@angular/material';
import {AnalyticsService} from '../analytics.service';

@Component({
  selector: 'app-add-column',
  templateUrl: './add-column.component.html',
  styleUrls: ['./add-column.component.css'],
  providers: [AnalyticsService]
})
export class AddColumnComponent  implements OnInit {
  public columnId = '';
  public name = '';
  public label = '';
  public type = '';
  public format = '';
  public primaryKey ='';
  public columnTypes = [{value: 'S', viewValue: 'String'}, {value: 'D', viewValue: 'Date'}
  , {value: 'I', viewValue: 'Integer'} , {value: 'F', viewValue: 'Decimal'}];
  public primaryKeyType = ['No','Yes'];

  constructor(public dialogRef: MatDialogRef<AddColumnComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private analyticsService: AnalyticsService) { }

  ngOnInit() {
      if ( this.data && this.data.columnId) {
        this.columnId = this.data.columnId;
        this.analyticsService.getReportColumn(this.data.columnId)
        .subscribe(data => {
               this.name = data.name;
               this.label = data.label;
               this.type = data.type;
               this.format = data.format;
               if (data.primaryKey == 1) {
                   this.primaryKey ='Yes';
               } else {
                  this.primaryKey ='No';
               }
        });

      }
  }

  onNoClick(): void {
    this.dialogRef.close(false);
  }

  addColumn(): void {
      var primarydataKey = 0;
      if (this.primaryKey == 'Yes') {
          primarydataKey = 1;
      } 
      this.analyticsService.
          addReportColumn(this.name , +this.data.reportId, this.label, this.type, this.format, primarydataKey )
            .subscribe(data => {
            this.dialogRef.close(true);
          });
  }

  editColumn(): void {
      var primarydataKey = 0;
      if (this.primaryKey == 'Yes') {
          primarydataKey = 1;
      } 
    this.analyticsService.
        updateReportColumn(+this.columnId, this.name , +this.data.reportId, this.label, this.type, this.format,primarydataKey)
          .subscribe(data => {
          this.dialogRef.close(true);
        });
  }


}
