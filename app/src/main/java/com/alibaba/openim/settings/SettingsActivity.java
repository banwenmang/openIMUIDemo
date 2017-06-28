package com.alibaba.openim.settings;


import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Toast;

import com.alibaba.openIMUIDemo.R;
import com.alibaba.tcms.PushActionConstants;
import com.alibaba.tcms.PushConstant;
import com.alibaba.tcms.client.ClientRegInfo;
import com.alibaba.tcms.client.ServiceChooseHelper;
import com.alibaba.tcms.service.TCMSService;
import com.alibaba.tcms.utils.PushLog;
import com.alibaba.wxlib.util.SysUtil;

import java.util.regex.Pattern;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    private static Context activity;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }

            if(preference.hasKey()){
                if("tcms_given_ip_key".equals(preference.getKey())) {
                    String ip = ((EditTextPreference)preference).getEditText().getText().toString().trim();
                    if(TextUtils.isEmpty(ip)) {
                        ip = stringValue;
                    }
                    if(TextUtils.isEmpty(ip)) {
                        switchTcmsConnIp("");
                        preference.setSummary("");
                        return true;
                    }
                    if(!ip.contains(":")) {
                        Toast.makeText(activity, "ip地址没有端口号!", Toast.LENGTH_LONG).show();
                        ((EditTextPreference)preference).getEditText().setText("");
                        preference.setSummary("");
                        return false;
                    }
                    String ipAdd = ip.substring(0, ip.lastIndexOf(":"));
                    Pattern pattern = Patterns.IP_ADDRESS;
                    if(pattern.matcher(ipAdd).find()) {
                        switchTcmsConnIp(ip);
                        preference.setSummary(ip);
                    } else {
                        Toast.makeText(activity, "ip地址设置格式错误!", Toast.LENGTH_LONG).show();
                        ((EditTextPreference)preference).getEditText().setText("");
                        preference.setSummary("");
                        return false;
                    }
                } else if ("app_key".equals(preference.getKey())){
                    String appKey = ((EditTextPreference)preference).getEditText().getText().toString().trim();

                }
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setupActionBar();
        activity = this;
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("push_ip_list_openim_online_key"));
            bindPreferenceSummaryToValue(findPreference("push_ip_list_dev_key"));
            bindPreferenceSummaryToValue(findPreference("config_tcms_storage"));
            bindPreferenceSummaryToValue(findPreference("tcms_given_ip_key"));
            findPreference("tcms_given_ip_key").setSummary(PreferenceManager.getDefaultSharedPreferences(activity).getString("tcms_given_ip_key", ""));
            bindPreferenceSummaryToValue(findPreference("app_key"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    private static void switchTcmsConnIp(String ip) {
        Intent intent = new Intent();
        ClientRegInfo info = ServiceChooseHelper.chooseService(SysUtil.sApp, true, true);
        String appName;
        if(info == null || TextUtils.isEmpty(info.appname)) {
            appName = SysUtil.sApp.getPackageName();
        } else {
            appName = info.appname;
        }
        ComponentName cn = new ComponentName(appName,
                TCMSService.class.getName());
        intent.putExtra("command", PushActionConstants.SWITCH_TCMS_CONN_IP + "?");
        intent.putExtra("ip", ip);
        intent.setComponent(cn);
        try {
            SysUtil.sApp.startService(intent);
        } catch (Exception e) {
        }
    }
}
