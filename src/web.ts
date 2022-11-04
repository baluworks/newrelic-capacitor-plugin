import { WebPlugin } from '@capacitor/core';

import type { NewRelicCapacitorPluginPlugin } from './definitions';

export class NewRelicCapacitorPluginWeb
  extends WebPlugin
  implements NewRelicCapacitorPluginPlugin
{
  start(options: { appKey: string; }): void {
    return;
  }
  async startInteraction(options: { value: string }): Promise<{ value: string; }> {
    return options;
  }
 
  endInteraction(options: { interactionId: string }): void {
    return;
  }
  
  setAttribute(options: { name: any; value: any }): void {
    return;
  }
  removeAttribute(options: { name: any }): void {
    return;
  }
  setUserId(options: { userId: string }): void {
    return;
  }

  recordBreadcrumb(options:{name: any, eventAttributes: object}): void {
    return;
  }
  recordCustomEvent(options:{eventType: any, eventName: any, attributes: object}): void {
    return;
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  crashNow(options: { message: string; }): void {
    return;
  }

  async currentSessionId(options: {}): Promise<{sessionId: string}> {
    return Promise.resolve({sessionId: "fake sessionId"});
  }

  incrementAttribute(options: { name: any; value: any; }): void {
    return;
  }

  noticeHttpTransaction(options: { url: string; method: string; status: any; startTime: any; endTime: any; bytesSent: any; bytesReceived: any; body: string; }): void {
    return;
  }

  noticeNetworkFailure(options: { url: string; method: string; status: any; startTime: any; endTime: any; failure: string; }): void {
    return;
  }

  recordMetric(options: { name: string; category: string; value: any; countUnit: string; valueUnit: string; }): void {
    return;
  }

  removeAllAttributes(options: {}): void {
    return;
  }

  setMaxEventBufferTime(options: { maxBufferTimeInSeconds: any; }): void {
    return;
  }

  setMaxEventPoolSize(options: { maxPoolSize: any; }): void {
    return;
  }
}

const defaultLog = window.console.log;
const defaultWarn = window.console.warn;
const defaultError = window.console.error;

console.log = function() {
  defaultLog.apply(console, );
};
console.warn = function() {
  defaultWarn.apply(console);
};
console.error = function() {
  defaultError.apply(console);
};
