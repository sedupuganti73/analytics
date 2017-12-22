import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import { HttpClient} from '@angular/common/http';

@Injectable()
export class AnalyticsService {
  prefix = '/api';
  constructor(private http: HttpClient) { }
  getReports(): Observable<any> {
    return this.http.get(this.prefix + '/reports/');
  }

  getReport(reportId: number): Observable<any> {
    return this.http.get(this.prefix + '/reports/' + reportId);
  }

  addReport( name: string, query: string, dataSource: number, createdBy: string ): Observable<any> {
    return this.http.post(this.prefix + '/reports/',
    { name: name,  query: query , label: '', dataSource: dataSource, createdBy: createdBy});
  }

  updateReport( reportId: number, name: string, query: string, dataSource: number, createdBy: string ): Observable<any> {
    return this.http.post(
      this.prefix + '/reports/',
      {reportId: reportId, name: name,  query: query , label: '', dataSource: dataSource, createdBy: createdBy});
  }

  deleteReport ( reportId: number): Observable<any> {
    return this.http.delete(this.prefix + '/reports/delete/' + reportId);
  }
  getReportColumn(columnId: number): Observable<any> {
    return this.http.get(this.prefix + '/columns/column/' + columnId);
  }
  getReportColumns(reportId: number): Observable<any> {
    return this.http.get(this.prefix + '/columns/' + reportId);
  }
  getHistory(reportId: string): Observable<any> {
    return this.http.get(this.prefix + '/history/' + reportId);
  }

  addReportColumn( name: string, reportId: number, label: string, type: string, format: string ): Observable<any> {
    return this.http.post(this.prefix + '/columns/create', { name: name, reportId: reportId, label: label, type: type, format: format});
  }

  updateReportColumn( columnId: number, name: string, reportId: number, label: string, type: string, format: string ): Observable<any> {
    return this.http.post(this.prefix +
      '/columns/create',
      { columnId: columnId, name: name, reportId: reportId, label: label, type: type, format: format});
  }

  deleteReportColumn ( columnId: number, reportId: number): Observable<any> {
    return this.http.delete(this.prefix + '/columns/delete/' + columnId + '/' + reportId);
  }

  getDatabases(): Observable<any> {
    return this.http.get(this.prefix + '/ds/');
  }
  getDatabase(dsId: number): Observable<any> {
    return this.http.get(this.prefix + '/ds/' + dsId);
  }
  addDatabase( name: string, description: string, url: string, username: string, password: string ): Observable<any> {
    return this.http.post(this.prefix + '/ds/',
    {name: name, description: description, url: url, dbUsername: username, dbPassword: password});
  }
  updateDatabase( dsId: number, name: string, description: string, url: string, username: string, password: string ): Observable<any> {
    return this.http.post(this.prefix + '/ds/',
    { dsId: dsId, name: name, description: description, url: url, dbUsername: username, dbPassword: password});
  }
  deleteDatabase ( dsId: number): Observable<any> {
    return this.http.delete(this.prefix + '/ds/' + dsId);
  }


}
