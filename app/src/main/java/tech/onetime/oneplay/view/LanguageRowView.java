package tech.onetime.oneplay.view;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import tech.onetime.oneplay.R;
import tech.onetime.oneplay.schema.LanguagePack;

/**
 * Created by Alexandro on 2016/7/12.
 */


@EViewGroup(R.layout.language_view)
public class LanguageRowView extends LinearLayout {

    @ViewById(R.id.languageTx)
    TextView languageTx;

    LanguagePack languagePack;
    iLanguageView iEvent;

    @ViewById(R.id.play_eq)
    ImageView speaker;

    public static  AnimationDrawable anim;

    public static void animStop(){
        anim.stop();
    }

    public static void animStart(){
        anim.start();
    }

    public LanguageRowView(Context context, LanguagePack languagePack) {
        super(context);
        this.languagePack = languagePack;
    }

    public void setiEvent(iLanguageView iEvent){
        this.iEvent = iEvent;
    }

    @AfterViews
    void afterViews(){
        languageTx.setText(languagePack.name);
    }

    @Click(R.id.root)
    void rootClicked(){
        if(iEvent!=null)
            iEvent.viewClicked(languagePack);
    }

    public void setClickedView(boolean clicked){
        speaker.setBackgroundResource(R.drawable.play);
        if(clicked) {
            languageTx.setTextColor(getResources().getColor(R.color.colorAccent));
            anim = (AnimationDrawable)speaker.getBackground();
            languageTx.setTypeface(null, Typeface.BOLD);
            speaker.setVisibility(View.VISIBLE);
            anim.start();
        }
        else {
            languageTx.setTextColor(getResources().getColor(R.color.gray));
            languageTx.setTypeface(null, Typeface.NORMAL);
            speaker.setVisibility(View.INVISIBLE);
        }
    }

    public interface iLanguageView{
        public void viewClicked(LanguagePack languagePack);
    }
}
