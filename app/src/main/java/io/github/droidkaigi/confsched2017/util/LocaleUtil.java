package io.github.droidkaigi.confsched2017.util;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.github.droidkaigi.confsched2017.BuildConfig;
import io.github.droidkaigi.confsched2017.R;
import io.github.droidkaigi.confsched2017.pref.DefaultPrefs;
import timber.log.Timber;

public class LocaleUtil {

    private static final Locale DEFAULT_LANG = Locale.ENGLISH;
    public static final List<Locale> SUPPORT_LANG = Arrays.asList(Locale.JAPANESE, Locale.ENGLISH);

    private static final String TAG = LocaleUtil.class.getSimpleName();

    private static final TimeZone CONFERENCE_TIMEZONE = TimeZone.getTimeZone(BuildConfig.CONFERENCE_TIMEZONE);

    public static void initLocale(Context context) {
        setLocale(context, getCurrentLanguageId(context));
    }

    public static void setLocale(Context context, String languageId) {
        Configuration config = new Configuration();
        DefaultPrefs.get(context).putLanguageId(languageId);
        Locale locale = new Locale(languageId);
        Locale.setDefault(locale);
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static String getCurrentLanguageId(Context context) {
        // This value would be stored language id or empty.
        String languageId = DefaultPrefs.get(context).getLanguageId();
        if (TextUtils.isEmpty(languageId)) {
            languageId = LocaleUtil.getLocaleLanguageId(Locale.getDefault());
        }

        for (Locale locale : SUPPORT_LANG) {
            if (TextUtils.equals(languageId, LocaleUtil.getLocaleLanguageId(locale))) {
                return languageId;
            }
        }

        return LocaleUtil.getLocaleLanguageId(DEFAULT_LANG);
    }

    public static String getLocaleLanguageId(@NonNull Locale locale) {
        return locale.getLanguage().toLowerCase();
    }

    public static String getCurrentLanguage(Context context) {
        return context.getString(getLanguage(LocaleUtil.getCurrentLanguageId(context)));
    }

    @StringRes
    public static int getLanguage(@NonNull Locale locale) {
        return getLanguage(getLocaleLanguageId(locale));
    }

    @StringRes
    public static int getLanguage(@NonNull String languageId) {
        if (TextUtils.equals(languageId, getLocaleLanguageId(Locale.ENGLISH))) {
            return R.string.lang_en;
        } else if (TextUtils.equals(languageId, getLocaleLanguageId(Locale.JAPANESE))) {
            return R.string.lang_ja;
        } else {
            return R.string.lang_en;
        }
    }

    public static Date getDisplayDate(@NonNull Date date, Context context) {
        DateFormat formatTokyo = SimpleDateFormat.getDateTimeInstance();
        formatTokyo.setTimeZone(CONFERENCE_TIMEZONE);
        DateFormat formatLocal = SimpleDateFormat.getDateTimeInstance();
        formatLocal.setTimeZone(getDisplayTimeZone(context));
        try {
            return formatLocal.parse(formatTokyo.format(date));
        } catch (ParseException e) {
            Timber.tag(TAG).e(e, "date: " + date + "can not parse.");
            return date;
        }
    }

    public static TimeZone getDisplayTimeZone(Context context) {
        TimeZone defaultTimeZone = TimeZone.getDefault();
        boolean shouldShowLocalTime = DefaultPrefs.get(context).getShowLocalTimeFlag();
        return (shouldShowLocalTime && defaultTimeZone != null) ? defaultTimeZone : CONFERENCE_TIMEZONE;
    }
}
