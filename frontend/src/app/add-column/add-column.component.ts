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
  public columnTypes = [{value: 'S', viewValue: 'String'}, {value: 'D', viewValue: 'Date'}
  , {value: 'I', viewValue: 'Integer'} , {value: 'F', viewValue: 'Decimal'}];

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
        });

      }
  }

  onNoClick(): void {
    this.dialogRef.close(false);
  }

  addColumn(): void {
      this.analyticsService.
          addReportColumn(this.name , +this.data.reportId, this.label, this.type, this.format )
            .subscribe(data => {
            this.dialogRef.close(true);
          });
  }

  editColumn(): void {
    this.analyticsService.
        updateReportColumn(+this.columnId, this.name , +this.data.reportId, this.label, this.type, this.format )
          .subscribe(data => {
          this.dialogRef.close(true);
        });
  }


}
