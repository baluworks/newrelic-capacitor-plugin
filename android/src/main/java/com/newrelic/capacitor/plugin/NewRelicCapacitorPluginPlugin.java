/*
 * Copyright (c) 2022-present New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0 
 */

package com.newrelic.capacitor.plugin;

import android.Manifest;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.newrelic.agent.android.ApplicationFramework;
import com.newrelic.agent.android.FeatureFlag;
import com.newrelic.agent.android.NewRelic;
import com.newrelic.agent.android.metric.MetricUnit;
import com.newrelic.agent.android.stats.StatsEngine;
import com.newrelic.agent.android.util.NetworkFailure;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@CapacitorPlugin(name = "NewRelicCapacitorPlugin", permissions = {
        @Permission(strings = { Manifest.permission.ACCESS_NETWORK_STATE }, alias = "network"),
        @Permission(strings = { Manifest.permission.INTERNET }, alias = "internet") })
public class NewRelicCapacitorPluginPlugin extends Plugin {

    private final NewRelicCapacitorPlugin implementation = new NewRelicCapacitorPlugin();
    private AgentConfig agentConfig;
    private static class AgentConfig {
        boolean analyticsEventEnabled;
        boolean crashReportingEnabled;
        boolean interactionTracingEnabled;
        boolean networkRequestEnabled;
        boolean networkErrorRequestEnabled;
        boolean httpResponseBodyCaptureEnabled;
        boolean loggingEnabled;
        String logLevel;
        String collectorAddress;
        String crashCollectorAddress;
        boolean sendConsoleEvents;

        public AgentConfig() {
            this.analyticsEventEnabled = true;
            this.crashReportingEnabled = true;
            this.interactionTracingEnabled = true;
            this.networkRequestEnabled = true;
            this.networkErrorRequestEnabled = true;
            this.httpResponseBodyCaptureEnabled = true;
            this.loggingEnabled = true;
            this.logLevel = "INFO";
            this.collectorAddress = "mobile-collector.newrelic.com";
            this.crashCollectorAddress = "mobile-crash.newrelic.com";
            this.sendConsoleEvents = true;
        }
    }

    @Override
    public void load() {
        super.load();
        agentConfig = new AgentConfig();
    }

    @PluginMethod
    public void start(PluginCall call) {
        String appKey = call.getString("appKey");
        JSObject agentConfiguration = call.getObject("agentConfiguration");

        if(appKey == null) {
            call.reject("Null appKey given to New Relic agent start");
            return;
        }

        boolean loggingEnabled = true;
        int logLevel = AgentLog.INFO;
        String collectorAddress = null;
        String crashCollectorAddress = null;

        if(agentConfiguration != null) {

            if(Boolean.FALSE.equals(agentConfiguration.getBool("analyticsEventEnabled"))) {
                NewRelic.disableFeature(FeatureFlag.AnalyticsEvents);
                agentConfig.analyticsEventEnabled = false;
            } else {
                NewRelic.enableFeature(FeatureFlag.AnalyticsEvents);
                agentConfig.analyticsEventEnabled = true;
            }

            if(Boolean.FALSE.equals(agentConfiguration.getBool("crashReportingEnabled"))) {
                NewRelic.disableFeature(FeatureFlag.CrashReporting);
                agentConfig.crashReportingEnabled = false;
            } else {
                NewRelic.enableFeature(FeatureFlag.CrashReporting);
                agentConfig.crashReportingEnabled = true;
            }

            if(Boolean.FALSE.equals(agentConfiguration.getBool("interactionTracingEnabled"))) {
                NewRelic.disableFeature(FeatureFlag.InteractionTracing);
                agentConfig.interactionTracingEnabled = false;
            } else {
                NewRelic.enableFeature(FeatureFlag.InteractionTracing);
                agentConfig.interactionTracingEnabled = true;
            }

            if(Boolean.FALSE.equals(agentConfiguration.getBool("networkRequestEnabled"))) {
                NewRelic.disableFeature(FeatureFlag.NetworkRequests);
                agentConfig.networkRequestEnabled = false;
            } else {
                NewRelic.enableFeature(FeatureFlag.NetworkRequests);
                agentConfig.networkRequestEnabled = true;
            }

            if(Boolean.FALSE.equals(agentConfiguration.getBool("networkErrorRequestEnabled"))) {
                NewRelic.disableFeature(FeatureFlag.NetworkErrorRequests);
                agentConfig.networkErrorRequestEnabled = false;
            } else {
                NewRelic.enableFeature(FeatureFlag.NetworkErrorRequests);
                agentConfig.networkErrorRequestEnabled = true;
            }

            if(Boolean.FALSE.equals(agentConfiguration.getBool("httpResponseBodyCaptureEnabled"))) {
                NewRelic.disableFeature(FeatureFlag.HttpResponseBodyCapture);
                agentConfig.httpResponseBodyCaptureEnabled = false;
            } else {
                NewRelic.enableFeature(FeatureFlag.HttpResponseBodyCapture);
                agentConfig.httpResponseBodyCaptureEnabled = true;
            }

            if(agentConfiguration.getBool("loggingEnabled") != null) {
                loggingEnabled = Boolean.TRUE.equals(agentConfiguration.getBool("loggingEnabled"));
                agentConfig.loggingEnabled = loggingEnabled;
            }

            if(agentConfiguration.getString("logLevel") != null) {
                Map<String, Integer> strToLogLevel = new HashMap<>();
                strToLogLevel.put("ERROR", AgentLog.ERROR);
                strToLogLevel.put("WARNING", AgentLog.WARNING);
                strToLogLevel.put("INFO", AgentLog.INFO);
                strToLogLevel.put("VERBOSE", AgentLog.VERBOSE);
                strToLogLevel.put("AUDIT", AgentLog.AUDIT);

                Integer configLogLevel = strToLogLevel.get(agentConfiguration.getString("logLevel"));
                if(configLogLevel != null) {
                    logLevel = configLogLevel;
                    agentConfig.logLevel = agentConfiguration.getString("logLevel");
                }

            }

            String newCollectorAddress = agentConfiguration.getString("collectorAddress");
            if(newCollectorAddress != null && !newCollectorAddress.isEmpty()) {
                collectorAddress = newCollectorAddress;
                agentConfig.collectorAddress = newCollectorAddress;
            }

            String newCrashCollectorAddress = agentConfiguration.getString("crashCollectorAddress");
            if(newCrashCollectorAddress != null && !newCrashCollectorAddress.isEmpty()) {
                crashCollectorAddress = newCrashCollectorAddress;
                agentConfig.crashCollectorAddress = newCrashCollectorAddress;
            }

            if(agentConfiguration.getBool("sendConsoleEvents") != null) {
                agentConfig.sendConsoleEvents = agentConfiguration.getBool("sendConsoleEvents");
            } 

        }

        // Use default collector addresses if not set
        if(collectorAddress == null && crashCollectorAddress == null) {
            NewRelic.withApplicationToken(appKey)
                    .withApplicationFramework(ApplicationFramework.Capacitor, "1.1.0")
                    .withLoggingEnabled(loggingEnabled)
                    .withLogLevel(logLevel)
                    .start(this.getActivity().getApplication());
        } else {
            if(collectorAddress == null) {
                collectorAddress = "mobile-collector.newrelic.com";
            }
            if(crashCollectorAddress == null) {
                crashCollectorAddress = "mobile-crash.newrelic.com";
            }
            NewRelic.withApplicationToken(appKey)
                    .withApplicationFramework(ApplicationFramework.Capacitor, "1.1.0")
                    .withLoggingEnabled(loggingEnabled)
                    .withLogLevel(logLevel)
                    .usingCollectorAddress(collectorAddress)
                    .usingCrashCollectorAddress(crashCollectorAddress)
                    .start(this.getActivity().getApplication());
        }

        call.resolve();
    }
    

    @PluginMethod
    public void setUserId(PluginCall call) {
        String value = call.getString("userId");

        if(value == null) {
            call.reject("Null userId given to setUserId");
            return;
        }

        NewRelic.setUserId(value);
        call.resolve();
    }

    @PluginMethod
    public void setAttribute(PluginCall call) {
        String name = call.getString("name");
        String value = call.getString("value");

        if(name == null || value == null) {
            call.reject("Null name or value given to setAttribute");
            return;
        }

        NewRelic.setAttribute(name, value);
        call.resolve();
    }

    @PluginMethod
    public void removeAttribute(PluginCall call) {
        String name = call.getString("name");

        if(name == null) {
            call.reject("Null name given to removeAttribute");
            return;
        }

        NewRelic.removeAttribute(name);
        call.resolve();
    }

    @PluginMethod
    public void recordBreadcrumb(PluginCall call) {
        String name = call.getString("name");
        JSONObject eventAttributes = call.getObject("eventAttributes");

        if(name == null) {
            call.reject("Null name given to recordBreadcrumb");
            return;
        }

        Map yourHashMap = new Gson().fromJson(String.valueOf(eventAttributes), Map.class);

        NewRelic.recordBreadcrumb(name, yourHashMap);
        call.resolve();
    }

    @PluginMethod
    public void recordCustomEvent(PluginCall call) {
        String name = call.getString("eventName");
        String eventType = call.getString("eventType");
        JSONObject attributes = call.getObject("attributes");

        if(eventType == null) {
            call.reject("Null eventType given to recordCustomEvent");
            return;
        }

        Map yourHashMap = new Gson().fromJson(String.valueOf(attributes), Map.class);

        NewRelic.recordCustomEvent(eventType, name, yourHashMap);
        call.resolve();
    }

    @PluginMethod
    public void startInteraction(PluginCall call) {
        String actionName = call.getString("value");

        if(actionName == null) {
            call.reject("Null value given to startInteraction");
            return;
        }

        JSObject ret = new JSObject();
        ret.put("value", NewRelic.startInteraction(actionName));
        call.resolve(ret);
    }

    @PluginMethod
    public void endInteraction(PluginCall call) {
        String interactionId = call.getString("interactionId");

        if(interactionId == null) {
            call.reject("Null interactionId given to endInteraction");
            return;
        }

        NewRelic.endInteraction(interactionId);
        call.resolve();
    }

    @PluginMethod
    public void crashNow(PluginCall call) {
        String message = call.getString("message");
        if (message == null) {
            NewRelic.crashNow();
        } else {
            NewRelic.crashNow(message);
        }
        call.resolve();
    }

    @PluginMethod
    public void currentSessionId(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("sessionId", NewRelic.currentSessionId());
        call.resolve(ret);
    }

    @PluginMethod
    public void incrementAttribute(PluginCall call) {
        String name = call.getString("name");
        Double value = call.getDouble("value");

        if (name == null) {
            call.reject("Bad name in incrementAttribute");
            return;
        }

        if (value == null) {
            NewRelic.incrementAttribute(name);
        } else {
            NewRelic.incrementAttribute(name, value);
        }
        call.resolve();
    }

    @PluginMethod
    public void noticeHttpTransaction(PluginCall call) {
        String url = call.getString("url");
        String method = call.getString("method");
        Integer status = call.getInt("status");
        Long startTime = call.getLong("startTime");
        Long endTime = call.getLong("endTime");
        Integer bytesSent = call.getInt("bytesSent");
        Integer bytesReceived = call.getInt("bytesReceived");
        String body = call.getString("body");

        if (url == null ||
                method == null ||
                status == null ||
                startTime == null ||
                endTime == null ||
                bytesSent == null ||
                bytesReceived == null) {
            call.reject("Bad parameters given to noticeHttpTransaction");
            return;
        }

        NewRelic.noticeHttpTransaction(url, method, status, startTime, endTime, bytesSent, bytesReceived, body);
        call.resolve();
    }

    @PluginMethod
    public void recordMetric(PluginCall call) {
        String name = call.getString("name");
        String category = call.getString("category");
        Double value = call.getDouble("value");
        String countUnit = call.getString("countUnit");
        String valueUnit = call.getString("valueUnit");

        if (name == null || category == null) {
            call.reject("Bad name or category in recordMetric");
            return;
        }

        if (value == null) {
            NewRelic.recordMetric(name, category);
            call.resolve();
        } else {
            if (countUnit == null && valueUnit == null) {
                NewRelic.recordMetric(name, category, value);
                call.resolve();
            } else {
                if (countUnit == null || valueUnit == null) {
                    call.reject("Both countUnit and valueUnit must be set in recordMetric");
                } else {
                    Map<String, MetricUnit> strToMetricUnit = new HashMap<>();
                    strToMetricUnit.put("PERCENT", MetricUnit.PERCENT);
                    strToMetricUnit.put("BYTES", MetricUnit.BYTES);
                    strToMetricUnit.put("SECONDS", MetricUnit.SECONDS);
                    strToMetricUnit.put("BYTES_PER_SECOND", MetricUnit.BYTES_PER_SECOND);
                    strToMetricUnit.put("OPERATIONS", MetricUnit.OPERATIONS);

                    if (strToMetricUnit.containsKey(countUnit) && strToMetricUnit.containsKey(valueUnit)) {
                        NewRelic.recordMetric(name, category, 1, value, value, strToMetricUnit.get(countUnit),
                                strToMetricUnit.get(valueUnit));
                        call.resolve();
                    } else {
                        call.reject(
                                "Bad countUnit or valueUnit in recordMetric. Must be one of: PERCENT, BYTES, SECONDS, BYTES_PER_SECOND, OPERATIONS");
                    }
                }
            }
        }
    }

    @PluginMethod
    public void removeAllAttributes(PluginCall call) {
        NewRelic.removeAllAttributes();
        call.resolve();
    }

    @PluginMethod
    public void setMaxEventBufferTime(PluginCall call) {
        Integer maxEventBufferTimeInSeconds = call.getInt("maxBufferTimeInSeconds");

        if (maxEventBufferTimeInSeconds == null) {
            call.reject("Bad maxBufferTimeInSeconds in setMaxEventBufferTime");
            return;
        }

        NewRelic.setMaxEventBufferTime(maxEventBufferTimeInSeconds);
        call.resolve();
    }

    @PluginMethod
    public void setMaxEventPoolSize(PluginCall call) {
        Integer maxPoolSize = call.getInt("maxPoolSize");

        if (maxPoolSize == null) {
            call.reject("Bad maxPoolSize in setMaxEventPoolSize");
            return;
        }

        NewRelic.setMaxEventPoolSize(maxPoolSize);
        call.resolve();
    }

    @PluginMethod
    public void recordError(PluginCall call) {
        String name = call.getString("name");
        String message = call.getString("message");
        String stack = call.getString("stack");
        Boolean isFatal = call.getBoolean("isFatal");

        if (name == null || stack == null) {
            call.reject("name should not be empty");
            return;
        }

        try {

            Map<String, Object> crashEvents = new HashMap<>();
            crashEvents.put("Name", name);
            crashEvents.put("Message", message);
            crashEvents.put("isFatal", isFatal);
            if (stack != null) {
                // attribute limit is 4096
                crashEvents.put("errorStack",
                        stack.length() > 4095 ? stack.substring(0, 4094) : stack);
            }

            NewRelic.recordBreadcrumb("JS Errors", crashEvents);
            NewRelic.recordCustomEvent("JS Errors", "JS Errors", crashEvents);

            StatsEngine.get().inc("Supportability/Mobile/Capacitor/JSError");

        } catch (IllegalArgumentException e) {
            Log.w("NRMA", e.getMessage());
        }
        call.resolve();
    }

    @PluginMethod
    public void analyticsEventEnabled(PluginCall call) {
        Boolean toEnable = call.getBoolean("enabled");

        if(toEnable == null) {
            call.reject("Bad parameter given to analyticsEventEnabled");
            return;
        }

        if(toEnable) {
            NewRelic.enableFeature(FeatureFlag.AnalyticsEvents);
        } else {
            NewRelic.disableFeature(FeatureFlag.AnalyticsEvents);
        }

        if(agentConfig != null) {
            agentConfig.analyticsEventEnabled = toEnable;
        }
        
        call.resolve();
    }

    @PluginMethod
    public void networkRequestEnabled(PluginCall call) {
        Boolean toEnable = call.getBoolean("enabled");

        if(toEnable == null) {
            call.reject("Bad parameter given to networkRequestEnabled");
            return;
        }

        if(toEnable) {
            NewRelic.enableFeature(FeatureFlag.NetworkRequests);
        } else {
            NewRelic.disableFeature(FeatureFlag.NetworkRequests);
        }

        if(agentConfig != null) {
            agentConfig.networkRequestEnabled = toEnable;
        }

        call.resolve();
    }

    @PluginMethod
    public void networkErrorRequestEnabled(PluginCall call) {
        Boolean toEnable = call.getBoolean("enabled");

        if(toEnable == null) {
            call.reject("Bad parameter given to networkErrorRequestEnabled");
            return;
        }

        if(toEnable) {
            NewRelic.enableFeature(FeatureFlag.NetworkErrorRequests);
        } else {
            NewRelic.disableFeature(FeatureFlag.NetworkErrorRequests);
        }

        if(agentConfig != null) {
            agentConfig.networkErrorRequestEnabled = toEnable;
        }

        call.resolve();
    }

    @PluginMethod
    public void httpResponseBodyCaptureEnabled(PluginCall call) {
        Boolean toEnable = call.getBoolean("enabled");

        if(toEnable == null) {
            call.reject("Bad parameter given to httpResponseBodyCaptureEnabled");
            return;
        }

        if(toEnable) {
            NewRelic.enableFeature(FeatureFlag.HttpResponseBodyCapture);
        } else {
            NewRelic.disableFeature(FeatureFlag.HttpResponseBodyCapture);
        }

        if(agentConfig != null) {
            agentConfig.httpResponseBodyCaptureEnabled = toEnable;
        }

        call.resolve();
    }

    @PluginMethod
    public void getAgentConfiguration(PluginCall call) {
        JSObject ret = new JSObject();
        // Returns empty object if plugin not loaded
        if (agentConfig != null) {
            ret.put("analyticsEventEnabled", agentConfig.analyticsEventEnabled);
            ret.put("crashReportingEnabled", agentConfig.crashReportingEnabled);
            ret.put("interactionTracingEnabled", agentConfig.interactionTracingEnabled);
            ret.put("networkRequestEnabled", agentConfig.networkRequestEnabled);
            ret.put("networkErrorRequestEnabled", agentConfig.networkErrorRequestEnabled);
            ret.put("httpResponseBodyCaptureEnabled", agentConfig.httpResponseBodyCaptureEnabled);
            ret.put("logLevel", agentConfig.logLevel);
            ret.put("collectorAddress", agentConfig.collectorAddress);
            ret.put("crashCollectorAddress", agentConfig.crashCollectorAddress);
            ret.put("sendConsoleEvents", agentConfig.sendConsoleEvents);
        }
        call.resolve(ret);
    }

}
