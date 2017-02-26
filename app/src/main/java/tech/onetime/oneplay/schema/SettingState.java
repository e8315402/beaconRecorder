package tech.onetime.oneplay.schema;

/**
 * Created by JianFa on 2017/2/26
 */

public class SettingState {

    private int _currentDistance = 1;
    private String _currentTxPower = "1M";
    private BeaconObject _currentBeaconObject = null;
    private String _fileName = null;

    private static SettingState _instance = new SettingState();

    private SettingState() {}

    public static SettingState getInstance() {
        return _instance;
    }

    public void set_currentDistance(int distance) {
        _currentDistance = distance;
    }

    public void set_currentTxPower(String txPower) {
        _currentTxPower = txPower;
    }

    public void set_currentBeaconObject(BeaconObject beaconObject) {
        _currentBeaconObject = beaconObject;
    }

    public void set_fileName(String fileName){
        _fileName = fileName;
    }

    public int get_currentDistance() {
        return _currentDistance;
    }

    public String get_currentTxPower() {
        return _currentTxPower;
    }

    public BeaconObject get_currentBeaconObject() {
        return _currentBeaconObject;
    }

    public String get_fileName() {
        return _fileName;
    }

}
