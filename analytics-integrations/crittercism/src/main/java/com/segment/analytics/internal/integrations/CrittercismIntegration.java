package com.segment.analytics.internal.integrations;

import com.crittercism.app.Crittercism;
import com.crittercism.app.CrittercismConfig;
import com.segment.analytics.Analytics;
import com.segment.analytics.ValueMap;
import com.segment.analytics.internal.AbstractIntegration;
import com.segment.analytics.internal.model.payloads.IdentifyPayload;
import com.segment.analytics.internal.model.payloads.ScreenPayload;
import com.segment.analytics.internal.model.payloads.TrackPayload;

import static com.segment.analytics.internal.Utils.isNullOrEmpty;

/**
 * Crittercism is an error reporting tool for your mobile apps. Any time your app crashes or
 * errors.
 * Crittercism will collect logs that will help you debug the problem and fix your app.
 *
 * @see <a href="http://crittercism.com">Crittercism</a>
 * @see <a href="https://segment.com/docs/integrations/crittercism">Crittercism Integration</a>
 * @see <a href="http://docs.crittercism.com/android/android.html">Crittercism Android SDK</a>
 */
public class CrittercismIntegration extends AbstractIntegration<Void> {

  static final String CRITTERCISM_KEY = "Crittercism";

  @Override public void initialize(Analytics analytics, ValueMap settings)
      throws IllegalStateException {
    CrittercismConfig config = new CrittercismConfig();

    config.setLogcatReportingEnabled(settings.getBoolean("shouldCollectLogcat", false));

    boolean includeVersionCode = settings.getBoolean("includeVersionCode", false);
    config.setVersionCodeToBeIncludedInVersionString(includeVersionCode);

    String customVersionName = settings.getString("customVersionName");
    if (!isNullOrEmpty(customVersionName)) {
      config.setCustomVersionName(customVersionName);
    }

    config.setServiceMonitoringEnabled(settings.getBoolean("enableServiceMonitoring", true));

    Crittercism.initialize(analytics.getApplication(), settings.getString("appId"), config);
  }

  @Override public Void getUnderlyingInstance() {
    return null;
  }

  @Override public String key() {
    return CRITTERCISM_KEY;
  }

  @Override public void identify(IdentifyPayload identify) {
    super.identify(identify);
    Crittercism.setUsername(identify.userId());
    Crittercism.setMetadata(identify.traits().toJsonObject());
  }

  @Override public void screen(ScreenPayload screen) {
    super.screen(screen);
    Crittercism.leaveBreadcrumb(String.format(VIEWED_EVENT_FORMAT, screen.event()));
  }

  @Override public void track(TrackPayload track) {
    super.track(track);
    Crittercism.leaveBreadcrumb(track.event());
  }

  @Override public void flush() {
    super.flush();
    Crittercism.sendAppLoadData();
  }
}
