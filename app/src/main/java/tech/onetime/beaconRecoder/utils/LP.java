package tech.onetime.beaconRecoder.utils;

import android.widget.LinearLayout;

public class LP {

	public final static int WRAP = LinearLayout.LayoutParams.WRAP_CONTENT;
	public final static int MATCH = LinearLayout.LayoutParams.MATCH_PARENT;

	public static LinearLayout.LayoutParams customParam(int sizeW, int sizeH) {
		LinearLayout.LayoutParams lp = null;

		lp = new LinearLayout.LayoutParams(sizeW, sizeH);

		return lp;
	}

	public static LinearLayout.LayoutParams customParam(int sizeW, int sizeH,
			float f) {
		LinearLayout.LayoutParams lp = null;

		lp = new LinearLayout.LayoutParams(sizeW, sizeH, f);

		return lp;
	}

	public static LinearLayout.LayoutParams Wrap_Wrap() {
		LinearLayout.LayoutParams lp = null;

		lp = new LinearLayout.LayoutParams(WRAP, WRAP);

		return lp;
	}

	public static LinearLayout.LayoutParams Wrap_Match() {
		LinearLayout.LayoutParams lp = null;

		lp = new LinearLayout.LayoutParams(WRAP, MATCH);

		return lp;
	}

	public static LinearLayout.LayoutParams Match_Wrap() {
		LinearLayout.LayoutParams lp = null;

		lp = new LinearLayout.LayoutParams(MATCH, WRAP);

		return lp;
	}

	public static LinearLayout.LayoutParams Match_Match() {
		LinearLayout.LayoutParams lp = null;

		lp = new LinearLayout.LayoutParams(MATCH, MATCH);

		return lp;
	}
}
