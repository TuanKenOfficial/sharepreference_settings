package com.example.hocsharepreferencesetting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;

import java.util.List;

/**
 * {@link PreferenceActivity} trình bày một bộ cài đặt ứng dụng. TRÊN
 * thiết bị cầm tay, cài đặt được trình bày dưới dạng một danh sách duy nhất. Trên máy tính bảng,
 * cài đặt được chia theo danh mục, với tiêu đề danh mục được hiển thị ở bên trái
 * danh sách các cài đặt.
 * <p>
 * Xem <a href="http://developer.android.com/design/patterns/settings.html">
 * Thiết kế Android: Cài đặt</a> cho nguyên tắc thiết kế và <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Cài đặt
 * Hướng dẫn API</a> để biết thêm thông tin về cách phát triển Giao diện người dùng cài đặt.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * Trình nghe thay đổi giá trị tùy chọn cập nhật tóm tắt của tùy chọn
     * để phản ánh giá trị mới của nó.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // Để biết tùy chọn danh sách, hãy tra cứu giá trị hiển thị chính xác trong
                // danh sách 'mục nhập' của tùy chọn.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Đặt tóm tắt để phản ánh giá trị mới.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // Để biết tùy chọn nhạc chuông, hãy tra cứu giá trị hiển thị chính xác
                // sử dụng Trình quản lý nhạc chuông.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Xóa tóm tắt nếu có lỗi tra cứu.
                        preference.setSummary(null);
                    } else {
                        // Đặt phần tóm tắt để phản ánh hiển thị nhạc chuông mới

                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);  // tên.
                    }
                }

            } else {
                // Đối với tất cả các tùy chọn khác, đặt tóm tắt thành giá trị
                // biểu diễn chuỗi đơn giản.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Phương pháp trợ giúp để xác định xem thiết bị có màn hình cực lớn hay không. Vì
     * ví dụ: máy tính bảng 10" là loại cực lớn.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Liên kết bản tóm tắt của tùy chọn với giá trị của nó. Cụ thể hơn, khi
     * giá trị tùy chọn được thay đổi, tóm tắt của nó (dòng văn bản bên dưới
     * tiêu đề ưu tiên) được cập nhật để phản ánh giá trị. Bản tóm tắt cũng
     * được cập nhật ngay lập tức khi gọi phương thức này. Định dạng hiển thị chính xác là
     * phụ thuộc vào loại ưu tiên.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Đặt trình nghe để theo dõi các thay đổi giá trị.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Kích hoạt trình nghe ngay lập tức bằng tùy chọn
        // giá trị hiện tại.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Thiết lập {@link android.app.ActionBar}, nếu API có sẵn.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Hiển thị nút Lên trên thanh hành động.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * Phương pháp này dừng việc tiêm phân đoạn vào các ứng dụng độc hại.
     * Hãy đảm bảo loại bỏ mọi đoạn không xác định ở đây.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * Đoạn này chỉ hiển thị các tùy chọn chung. Nó được sử dụng khi
     * Hoạt động đang hiển thị giao diện người dùng cài đặt hai ngăn.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Liên kết các bản tóm tắt của các tùy chọn EditText/List/Dialog/Ringtone
            // theo giá trị của chúng. Khi giá trị của chúng thay đổi, tóm tắt của chúng là
            // được cập nhật để phản ánh giá trị mới, theo Thiết kế Android
            // hướng dẫn.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
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

    /**
     * Đoạn này chỉ hiển thị các tùy chọn thông báo. Nó được sử dụng khi
     * Hoạt động đang hiển thị giao diện người dùng cài đặt hai ngăn.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Liên kết các bản tóm tắt của các tùy chọn EditText/List/Dialog/Ringtone
            // theo giá trị của chúng. Khi giá trị của chúng thay đổi, tóm tắt của chúng là
            // được cập nhật để phản ánh giá trị mới, theo Thiết kế Android
            // hướng dẫn.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
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

    /**
     * Đoạn này chỉ hiển thị dữ liệu và tùy chọn đồng bộ hóa. Nó được sử dụng khi
     * Hoạt động đang hiển thị giao diện người dùng cài đặt hai ngăn.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);
            // Liên kết các bản tóm tắt của các tùy chọn EditText/List/Dialog/Ringtone
            // theo giá trị của chúng. Khi giá trị của chúng thay đổi, tóm tắt của chúng là
            // được cập nhật để phản ánh giá trị mới, theo Thiết kế Android
            // hướng dẫn.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
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
}