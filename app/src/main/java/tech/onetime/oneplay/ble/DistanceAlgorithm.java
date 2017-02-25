package tech.onetime.oneplay.ble;

import java.util.ArrayList;

import tech.onetime.oneplay.schema.DisplayObject;
import tech.onetime.oneplay.schema.OnePlayMicroApp;

/**
 * Created by Alexandro on 2016/7/21.
 */
public class DistanceAlgorithm {

    private double[] distances;
    private ArrayList<DisplayObject> playTargets = new ArrayList<>();
    private int highRelationCount = 0;
    private DisplayAroundType playSelectionType;

    public static enum DisplayAroundType{ONLY, MULTI, NO_DISPLAY}

    public DistanceAlgorithm(OnePlayMicroApp microApp, double[] currentPosition){

        calculate(microApp, currentPosition);

    }

    private void calculate(OnePlayMicroApp microApp, double[] currentPosition){

        distances = new double[microApp.displayObjects.size()];

//        highRelationCount = 0;
        playTargets = new ArrayList<>();

        for (int i = 0; i < distances.length; i++) {
            try {
                DisplayObject display = microApp.displayObjects.get(i);
                distances[i] = Math.sqrt(Math.pow(currentPosition[0] - display.x, 2) + Math.pow(currentPosition[1] - display.y, 2));

                display.testDistance = distances[i];

                if (distances[i] <= microApp.LOWER) {
                    playTargets.add(0, display);
                }

//                if (distances[i] <= microApp.LOWER) {
//                    playTargets.add(0, display);
//                    highRelationCount++;
//                } else if (distances[i] <= microApp.UPPER) {
//                    playTargets.add(display);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        playSelectionType = DisplayAroundType.MULTI;
//        if (highRelationCount == 1) { // lower 內只有一個
//            playSelectionType = DisplayAroundType.ONLY;
//            //System.out.println("播放一項");
//        } else if (highRelationCount > 1) {   // lower 內有多個
//            playSelectionType = DisplayAroundType.MULTI;
//            //System.out.println("顯示選項");
//        } else {    // 全部都在 upper 但 全部都不再 lower
//            playSelectionType = DisplayAroundType.MULTI;
//            //System.out.println("顯示 upper 選項");
//        }

        if (playTargets.size() == 0)
            playSelectionType = DisplayAroundType.NO_DISPLAY;
    }

    public ArrayList<DisplayObject> getDisplayAroundList(){
        return this.playTargets;
    }

    public DisplayAroundType getType(){
        return playSelectionType;
    }

}
