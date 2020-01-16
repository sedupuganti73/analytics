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

  addReport( name: string, query: string, dataSource: number,sfdcDataSource: number, createdBy: string, loadType: string,methodType: string, priority: string ,runTime: string ,recordCountQuery: string ,isIncremental: boolean,appName: string, flowName: string  ): Observable<any> {
    return this.http.post(this.prefix + '/reports/',
    { name: name,  query: query , label: '', dataSource: dataSource,sfdcDataSource: sfdcDataSource, createdBy: createdBy, type: loadType,methodType: methodType,priority: priority, runTime: runTime, recordCountQuery: recordCountQuery, isIncremental: isIncremental, appName: appName,flowName: flowName});
  }

  updateReport( reportId: number, name: string, query: string, dataSource: number,sfdcDataSource: number, createdBy: string, loadType: string,methodType: string, priority: string ,runTime: string ,recordCountQuery: string ,isIncremental: boolean,appName: string, flowName: string ): Observable<any> {
    return this.http.post(
      this.prefix + '/reports/',
      {reportId: reportId, name: name,  query: query , label: '', dataSource: dataSource,sfdcDataSource: sfdcDataSource, createdBy: createdBy, type: loadType,methodType: methodType, priority: priority, runTime: runTime, recordCountQuery: recordCountQuery, isIncremental: isIncremental, appName: appName,flowName: flowName});
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
  getHistory(reportId: number): Observable<any> {
    return this.http.get(this.prefix + '/history/' + reportId);
  }
  
  
  addReportColumn( name: string, reportId: number, label: string, type: string, format: string, primaryKey: number,precision: number,scale: number ): Observable<any> {
    return this.http.post(this.prefix + '/columns/create', { name: name, reportId: reportId, label: label, type: type, format: format, primaryKey: primaryKey,precision: precision,scale: scale});
  }

  updateReportColumn( columnId: number, name: string, reportId: number, label: string, type: string, format: string, primaryKey: number,precision: number,scale: number ): Observable<any> {
    return this.http.post(this.prefix +
      '/columns/create',
      { columnId: columnId, name: name, reportId: reportId, label: label, type: type, format: format, primaryKey: primaryKey,precision: precision,scale: scale});
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
  runReport( reportId: number ): Observable<any> {
    return this.http.get(this.prefix + '/load/' + reportId);
  }
  
  getForceDatabases(): Observable<any> {
    return this.http.get(this.prefix + '/forceDs/');
  }
  getForceDatabase(dsId: number): Observable<any> {
    return this.http.get(this.prefix + '/forceDs/' + dsId);
  }
  addForceDatabase( name: string, description: string, url: string, username: string, password: string ): Observable<any> {
    return this.http.post(this.prefix + '/forceDs/',
    {name: name, description: description, url: url, username: username, password: password});
  }
  updateForceDatabase( dsId: number, name: string, description: string, url: string, username: string, password: string ): Observable<any> {
    return this.http.post(this.prefix + '/forceDs/',
    { dsId: dsId, name: name, description: description, url: url, username: username, password: password});
  }
  deleteForceDatabase ( dsId: number): Observable<any> {
    return this.http.delete(this.prefix + '/forceDs/' + dsId);
  }  

}
