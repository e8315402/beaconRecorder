package tech.onetime.oneplay.connect;

import android.content.Context;

public interface iRestConnect {
    /**
     * 設定app的版本號 (http header Accept需要).
     */
    public String getAppVersion(Context ctx);
}