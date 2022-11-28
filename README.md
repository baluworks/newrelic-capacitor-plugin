[![Community Plus header](https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Community_Plus.png)](https://opensource.newrelic.com/oss-category/#community-plus)

# newrelic-capacitor-plugin

NewRelic Plugin for ionic Capacitor.This plugin uses native New Relic Android and iOS agents to instrument the Ionic Capacitor environment. The New Relic SDKs collect crashes, network traffic, and other information for hybrid apps using native components.


## Features
* Capture JavaScript errors
* Network Instrumentation
* Distributed Tracing
* Tracking console log, warn and error
* Promise rejection tracking
* Capture interactions and the sequence in which they were created
* Pass user information to New Relic to track user sessions

## Current Support:
- Android API 24+
- iOS 10
- Depends on New Relic iOS/XCFramework and Android agents

## Install

```bash
npm install newrelic-capacitor-plugin
npx cap sync
```


## Ionic Capacitor Setup

Now open your `index.tsx` and add the following code to launch NewRelic (don't forget to put proper application tokens):

``` tsx
import { NewRelicCapacitorPlugin } from 'newrelic-capacitor-plugin';
import { Capacitor } from '@capacitor/core';

    var appToken;

    if(Capacitor.getPlatform() === 'ios') 
        appToken = '<IOS-APP-TOKEN>';
    } else {
        appToken = '<ANDROID-APP-TOKEN>';
    }

NewRelicCapacitorPlugin.start({appKey:appToken})


```
AppToken is platform-specific. You need to generate the seprate token for Android and iOS apps.

### Android Setup
1. Install the New Relic native Android agent ([instructions here](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/install-configure/install-android-apps-gradle-android-studio)).
2. Update `build.gradle`:
  ```groovy
    buildscript {
      ...
      repositories {
        ...
        mavenCentral()
      }
      dependencies {
        ...
        classpath "com.newrelic.agent.android:agent-gradle-plugin:6.8.0"
      }
    }
  ```

3. Update `app/build.gradle`:
  ```groovy
    apply plugin: "com.android.application"
    apply plugin: 'newrelic' // <-- add this
  
  ```

4. Make sure your app requests INTERNET and ACCESS_NETWORK_STATE permissions by adding these lines to your `AndroidManifest.xml`
  ```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
  ```
To automatically link the package, rebuild your project:
```shell
# Android apps
ionic capacitor run android

# iOS apps
ionic capacitor run ios
```
## API


* [`setUserId(...)`](#setuserid)
* [`setAttribute(...)`](#setattribute)
* [`removeAttribute(...)`](#removeattribute)
* [`recordBreadcrumb(...)`](#recordbreadcrumb)
* [`recordCustomEvent(...)`](#recordcustomevent)
* [`startInteraction(...)`](#startinteraction)
* [`endInteraction(...)`](#endinteraction)
* [`crashNow(...)`](#crashnow)
* [`currentSessionId(...)`](#currentsessionid)
* [`incrementAttribute(...)`](#incrementattribute)
* [`noticeHttpTransaction(...)`](#noticehttptransaction)
* [`noticeNetworkFailure(...)`](#noticenetworkfailure)
* [`recordMetric(...)`](#recordmetric)
* [`removeAllAttributes(...)`](#removeallattributes)
* [`setMaxEventBufferTime(...)`](#setmaxeventbuffertime)
* [`setMaxEventPoolSize(...)`](#setmaxeventpoolsize)
* [`recordError(...)`](#recorderror)
* [`analyticsEventEnabled(...)`](#analyticseventenabled)
* [`networkRequestEnabled(...)`](#networkrequestenabled)
* [`networkErrorRequestEnabled(...)`](#networkerrorrequestenabled)
* [`httpRequestBodyCaptureEnabled(...)`](#httprequestbodycaptureenabled)




### [setUserId(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/set-user-id)
> Set a custom user identifier value to associate user sessions with analytics events and attributes.
```typescript
setUserId(options: { userId: string; }) => void
```

| Param         | Type                             |
| ------------- | -------------------------------- |
| **`options`** | <code>{ userId: string; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.setUserId({ userId: "CapacitorUserId" });
```

--------------------


### [setAttribute(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/set-attribute)
> Creates a session-level attribute shared by multiple mobile event types. Overwrites its previous value and type each time it is called.
```typescript
setAttribute(options: { name: string; value: string; }) => void
```

| Param         | Type                                    |
| ------------- | --------------------------------------- |
| **`options`** | <code>{ name: string; value: string; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.setAttribute({ name: "CapacitorAttribute", value: "123" });
```

--------------------


### [removeAttribute(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/remove-attribute)
> This method removes the attribute specified by the name string.

```typescript
removeAttribute(options: { name: string; }) => void
```

| Param         | Type                        |
| ------------- | --------------------------- |
| **`options`** | <code>{ name: string; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.removeAttribute({ name: "CapacitorAttribute" });
```
--------------------


### [recordBreadcrumb(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/recordbreadcrumb)
> Track app activity/screen that may be helpful for troubleshooting crashes.

```typescript
recordBreadcrumb(options: { name: string; eventAttributes: object; }) => void
```

| Param         | Type                                                 |
| ------------- | ---------------------------------------------------- |
| **`options`** | <code>{ name: string; eventAttributes: object; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.recordBreadcrumb({ name: "shoe", eventAttributes: {"shoeColor": "blue","shoesize": 9,"shoeLaces": true} });
```

--------------------


### [recordCustomEvent(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/recordcustomevent-android-sdk-api)
> Creates and records a custom event for use in New Relic Insights.

```typescript
recordCustomEvent(options: { eventType: string; eventName: string; attributes: object; }) => void
```

| Param         | Type                                                                 |
| ------------- | -------------------------------------------------------------------- |
| **`options`** | <code>{ eventType: string; eventName: string; attributes: object; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.recordCustomEvent({ eventType: "mobileClothes", eventName: "pants", attributes:{"pantsColor": "blue","pantssize": 32,"belt": true} });
```

--------------------


### [startInteraction(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/start-interaction)
> Track a method as an interaction.

```typescript
startInteraction(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### [endInteraction(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/end-interaction)
> End an interaction
> (Required). This uses the string ID for the interaction you want to end.
> This string is returned when you use startInteraction().
```typescript
endInteraction(options: { interactionId: string; }) => void
```

| Param         | Type                                    |
| ------------- | --------------------------------------- |
| **`options`** | <code>{ interactionId: string; }</code> |

#### Usage:
```ts
 const badApiLoad = async () => {
   const id = await NewRelicCapacitorPlugin.startInteraction({ value: 'StartLoadBadApiCall' });
   console.log(id);
   const url = 'https://fakewebsite.com/moviessssssssss.json';
   fetch(url)
     .then((response) => response.json())
     .then((responseJson) => {
       console.log(responseJson);
       NewRelicCapacitorPlugin.endInteraction({ interactionId: id });
     }) .catch((error) => {
       NewRelicCapacitorPlugin.endInteraction({ interactionId: id });
       console.error(error);
     });
 };
```

--------------------


### [crashNow(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/crashnow-android-sdk-api)
> Throws a demo run-time exception to test New Relic crash reporting.

```typescript
crashNow(options?: { message: string; } | undefined) => void
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ message: string; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.crashNow();
    NewRelicCapacitorPlugin.crashNow({ message: "A demo crash message" });
```
--------------------


### [currentSessionId(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/currentsessionid-android-sdk-api)
> Returns the current session ID. This method is useful for consolidating monitoring of app data (not just New Relic data) based on a single session definition and identifier.
```typescript
currentSessionId(options?: {} | undefined) => Promise<{ sessionId: string; }>
```

| Param         | Type            |
| ------------- | --------------- |
| **`options`** | <code>{}</code> |

**Returns:** <code>Promise&lt;{ sessionId: string; }&gt;</code>

#### Usage:
```ts
    let { sessionId } = await NewRelicCapacitorPlugin.currentSessionId();
```
--------------------


### [incrementAttribute(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/increment-attribute)
> Increments the count of an attribute with a specified name. Overwrites its previous value and type each time it is called. If the attribute does not exists, it creates a new attribute. If no value is given, it increments the value by 1.

```typescript
incrementAttribute(options: { name: string; value?: number; }) => void
```

| Param         | Type                                           |
| ------------- | ---------------------------------------------- |
| **`options`** | <code>{ name: string; value?: number; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.incrementAttribute({ name: 'CapacitorAttribute', value: 15 })
```

--------------------


### [noticeHttpTransaction(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/notice-http-transaction)
> Manually records HTTP transactions, with an option to also send a response body.
```typescript
noticeHttpTransaction(options: { url: string; method: string; status: number; startTime: number; endTime: number; bytesSent: number; bytesReceived: number; body: string; }) => void
```

| Param         | Type                                                                                                                                                      |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ url: string; method: string; status: number; startTime: number; endTime: number; bytesSent: number; bytesReceived: number; body: string; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.noticeHttpTransaction({
      url: "https://fakewebsite.com",
      method: "GET",
      status: 200,
      startTime: Date.now(),
      endTime: Date.now(),
      bytesSent: 10,
      bytesReceived: 2500,
      body: "fake http response body 200",
    });
```
--------------------


### [noticeNetworkFailure(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/notice-network-failure)
> Records network failures. If a network request fails, use this method to record details about the failures. In most cases, place this call inside exception handlers, such as catch blocks. Supported failures are: `Unknown`, `BadURL`, `TimedOut`, `CannotConnectToHost`, `DNSLookupFailed`, `BadServerResponse`, `SecureConnectionFailed`


```typescript
noticeNetworkFailure(options: { url: string; method: string; status: number; startTime: number; endTime: number; failure: string; }) => void
```

| Param         | Type                                                                                                               |
| ------------- | ------------------------------------------------------------------------------------------------------------------ |
| **`options`** | <code>{ url: string; method: string; status: number; startTime: number; endTime: number; failure: string; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.noticeNetworkFailure({
        url: "https://networkfailurewebsite.com",
        method: "GET",
        status: 404,
        startTime: Date.now(),
        endTime: Date.now(),
        failure: "Unknown",
    });
```

--------------------


### [recordMetric(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/recordmetric-android-sdk-api))
> Records custom metrics (arbitrary numerical data), where countUnit is the measurement unit of the metric count and valueUnit is the measurement unit for the metric value. If using countUnit or valueUnit, then all of value, countUnit, and valueUnit must all be set. Supported measurements for countUnit and valueUnit are: `PERCENT`, `BYTES`, `SECONDS`, `BYTES_PER_SECOND`, `OPERATIONS`

```typescript
recordMetric(options: { name: string; category: string; value?: number; countUnit?: string; valueUnit?: string; }) => void
```

| Param         | Type                                                                                                     |
| ------------- | -------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ name: string; category: string; value?: number; countUnit?: string; valueUnit?: string; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.recordMetric({
      name: "CapacitorMetricName",
      category: "CapacitorMetricCategory",
    });
    NewRelicCapacitorPlugin.recordMetric({
      name: "CapacitorMetricName2",
      category: "CapacitorMetricCategory2",
      value: 25,
    });
    NewRelicCapacitorPlugin.recordMetric({
      name: "CapacitorMetricName3",
      category: "CapacitorMetricCategory3",
      value: 30,
      countUnit: "SECONDS",
      valueUnit: "OPERATIONS",
    });
```

--------------------


### [removeAllAttributes(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/remove-all-attributes)
> Removes all attributes from the session

```typescript
removeAllAttributes(options?: {} | undefined) => void
```

| Param         | Type            |
| ------------- | --------------- |
| **`options`** | <code>{}</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.removeAllAttributes();
```

--------------------


### [setMaxEventBufferTime(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/set-max-event-buffer-time)
> Sets the event harvest cycle length. Default is 600 seconds (10 minutes). Minimum value can not be less than 60 seconds. Maximum value should not be greater than 600 seconds.

```typescript
setMaxEventBufferTime(options: { maxBufferTimeInSeconds: number; }) => void
```

| Param         | Type                                             |
| ------------- | ------------------------------------------------ |
| **`options`** | <code>{ maxBufferTimeInSeconds: number; }</code> |

#### Usage: 
```ts
    NewRelicCapacitorPlugin.setMaxEventBufferTime({ maxBufferTimeInSeconds: 60 });
```
--------------------


### [setMaxEventPoolSize(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/set-max-event-pool-size)
> Sets the maximum size of the event pool stored in memory until the next harvest cycle. Default is a maximum of 1000 events per event harvest cycle. When the pool size limit is reached, the agent will start sampling events, discarding some new and old, until the pool of events is sent in the next harvest cycle.

```typescript
setMaxEventPoolSize(options: { maxPoolSize: number; }) => void
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ maxPoolSize: number; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.setMaxEventPoolSize({ maxPoolSize: 2000 })
```
--------------------


### recordError(...)
> Records JavaScript/TypeScript errors for Ionic Capacitor.

```typescript
recordError(options: { name: string; message: string; stack: string; isFatal: boolean; }) => void
```

| Param         | Type                                                                             |
| ------------- | -------------------------------------------------------------------------------- |
| **`options`** | <code>{ name: string; message: string; stack: string; isFatal: boolean; }</code> |

#### Usage:
```ts
    try {
      throw new Error('Example error message');
    } catch (e: any) {
      NewRelicCapacitorPlugin.recordError({
        name: e.name,
        message: e.message,
        stack: e.stack,
        isFatal: false,
      });
    }
```

--------------------


### [analyticsEventEnabled(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/android-agent-configuration-feature-flags/#ff-analytics-events)
> FOR ANDROID ONLY. Enable or disable the collecton of event data. This is set to true by default.

```typescript
analyticsEventEnabled(options: { enabled: boolean; }) => void
```

| Param         | Type                               |
| ------------- | ---------------------------------- |
| **`options`** | <code>{ enabled: boolean; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.analyticsEventEnabled({ enabled: true })
```

--------------------


### [networkRequestEnabled(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/android-agent-configuration-feature-flags/#ff-networkRequests)
> Enable or disable reporting successful HTTP requests to the MobileRequest event type. This is set to true by default.

```typescript
networkRequestEnabled(options: { enabled: boolean; }) => void
```

| Param         | Type                               |
| ------------- | ---------------------------------- |
| **`options`** | <code>{ enabled: boolean; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.networkRequestEnabled({ enabled: true })
```
--------------------


### [networkErrorRequestEnabled(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/android-agent-configuration-feature-flags/#ff-networkErrorRequests)
> Enable or disable reporting network and HTTP request errors to the MobileRequestError event type.

```typescript
networkErrorRequestEnabled(options: { enabled: boolean; }) => void
```

| Param         | Type                               |
| ------------- | ---------------------------------- |
| **`options`** | <code>{ enabled: boolean; }</code> |

#### Usage:
```ts
    NewRelicCapacitorPlugin.networkErrorRequestEnabled({ enabled: true })
```

--------------------


### [httpRequestBodyCaptureEnabled(...)](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/android-sdk-api/android-agent-configuration-feature-flags/#ff-withHttpResponseBodyCaptureEnabled)
> Enable or disable capture of HTTP response bodies for HTTP error traces, and MobileRequestError events.

```typescript
httpRequestBodyCaptureEnabled(options: { enabled: boolean; }) => void
```

| Param         | Type                               |
| ------------- | ---------------------------------- |
| **`options`** | <code>{ enabled: boolean; }</code> |
#### Usage:
```ts
    NewRelicCapacitorPlugin.httpRequestBodyCaptureEnabled({ enabled: true })
```

--------------------