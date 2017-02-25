package tech.onetime.oneplay.view;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.ArrayList;

import tech.onetime.oneplay.schema.LanguagePack;

/**
 * Created by Alexandro on 2016/7/13.
 */
public class LanguageViews extends LinearLayout {

    iLanguageViews iEvent;
    private ArrayList<LanguagePack> languagePacks = new ArrayList<LanguagePack>();

    public LanguageViews(Context context, ArrayList<LanguagePack> languagePacks, iLanguageViews iEvent) {
        super(context);
        this.iEvent = iEvent;
        this.languagePacks = languagePacks;
        this.setOrientation(VERTICAL);

        createLanguageViews();
    }

    private void createLanguageViews() {

        for (final LanguagePack languagePack : languagePacks) {
            final LanguageRowView lv = LanguageRowView_.build(getContext(), languagePack);
            lv.setiEvent(new LanguageRowView.iLanguageView() {
                @Override
                public void viewClicked(LanguagePack languagePack) {
                    if (iEvent != null) {
                        clearSelectedBg();
                        lv.setClickedView(true);
                        iEvent.languageSelected(languagePack);
                    }
                }
            });
            addView(lv);
        }
    }

    private void clearSelectedBg() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof LanguageRowView) {
                ((LanguageRowView) getChildAt(i)).setClickedView(false);
            }
        }
    }

    /**
     * 自動執行第一個view的事件
     */
    public void performFirstView() {
        if (getChildCount() == 0)
            return;
        if (getChildAt(0) instanceof LanguageRowView) {
            ((LanguageRowView) getChildAt(0)).rootClicked();
        }
    }

    /**
     * 傳入上次選擇的language(String)，並從 listview 中執行 performeClick
     *
     * @return true: listView中有符合的language. false:反之
     */
    public boolean performSelectedLanguage(String language) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof LanguageRowView) {
                LanguageRowView row = ((LanguageRowView) getChildAt(i));
                if (language.equals(row.languagePack.name)) {
                    row.rootClicked();
                    return true;
                }
            }
        }
        return false;
    }

    public interface iLanguageViews {
        /**
         * name 被選取
         */
        public void languageSelected(LanguagePack languagePack);
    }
}
