package tech.onetime.beaconRecorder.schema;

/**
 * Created by JianFa on 2017/2/26
 */

public class SettingState {

    private int _currentDistance = 1;
    private String _currentTxPower = "1M";
    private BeaconObject _currentBeaconObject = null;
    private String _fileName = null;

    private int[] distances = {1, 2, 3, 5, 8, 13, 20, 30, 40, 50};
    private String[] txPowers = {"1M", "10M", "20M", "50M"};

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

    public void set_theNextDistance() {

        if(_currentDistance != 50) {

            int distanceIndex = 0;
            while (distanceIndex < 10)
                if(distances[distanceIndex++] == _currentDistance) break;

            _currentDistance = distances[distanceIndex];

        }

    }

    public void set_theNextTxPower() {

        if(_currentTxPower.compareTo("50M") != 0) {

            int txPowerIndex = 0;
            while(txPowerIndex < 4)
                if(txPowers[txPowerIndex++].compareTo(_currentTxPower) == 0) break;

            _currentTxPower = txPowers[txPowerIndex];

        }

    }

}
