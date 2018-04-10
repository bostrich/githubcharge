package com.hodanet.charge.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.BatteryManager;

import com.hodanet.charge.event.BatteryChangeEvent;
import com.hodanet.charge.event.BatteryConnectEvent;
import com.hodanet.charge.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;

/**
 *
 */

public class BatteryBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            //**************************电池状态************************************
            case Intent.ACTION_BATTERY_CHANGED:
                BatteryChangeEvent event = new BatteryChangeEvent();
                DecimalFormat decimalFormat = new DecimalFormat("#0.0");
                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
                LogUtil.d("BatteryState", "====================================================");
                LogUtil.d("BatteryState", "voltage=" + voltage);
                String batteryVoltage = decimalFormat.format((double) voltage / 1000) + "V";
                event.setBatteryVoltage(batteryVoltage);
                String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);//电池工艺
                LogUtil.d("BatteryState", "technology=" + technology);

                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                LogUtil.d("BatteryState", "temperature=" + temperature);
                String batteryTem = decimalFormat.format((double) temperature / 10) + "℃";
                event.setBatteryTem(batteryTem);
                //充电状态
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
                event.setStatus(status);

                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                LogUtil.d("BatteryState", "level=" + level);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                LogUtil.d("BatteryState", "scale=" + scale);
                int levelPercent = (int) (((float) level / scale) * 100);//电量百分比
                LogUtil.d("BatteryState", "levelPercent=" + levelPercent + "%");
                event.setPercent(levelPercent);
                String batteryPercent = levelPercent + "%";
                event.setBatteryPercent(batteryPercent);
                int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
                event.setBatteryHealth(health);
                switch (health) {
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        LogUtil.d("BatteryState", "BATTERY_HEALTH_GOOD");
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        LogUtil.d("BatteryState", "BATTERY_HEALTH_OVERHEAT");
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        LogUtil.d("BatteryState", "BATTERY_HEALTH_DEAD");
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        LogUtil.d("BatteryState", "BATTERY_HEALTH_OVER_VOLTAGE");
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        LogUtil.d("BatteryState", "BATTERY_HEALTH_UNSPECIFIED_FAILURE");

                        break;
                    case BatteryManager.BATTERY_HEALTH_COLD:
                        LogUtil.d("BatteryState", "BATTERY_HEALTH_COLD");

                        break;
                    default:
                        break;
                }
                EventBus.getDefault().post(event);
                break;
            case Intent.ACTION_BATTERY_LOW:
                LogUtil.d("BatteryState", "ACTION_BATTERY_LOW");

                break;
            case Intent.ACTION_BATTERY_OKAY:
                LogUtil.d("BatteryState", "ACTION_BATTERY_OKAY");

                break;
            case Intent.ACTION_POWER_CONNECTED:
                LogUtil.d("BatteryState", "ACTION_POWER_CONNECTED");
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, BatteryManager.BATTERY_PLUGGED_AC);
                switch (plugged) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        LogUtil.d("BatteryState", "充电方式=BATTERY_PLUGGED_AC");
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        LogUtil.d("BatteryState", "充电方式=BATTERY_PLUGGED_USB");
                        break;
                    case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                        LogUtil.d("BatteryState", "充电方式=BATTERY_PLUGGED_WIRELESS");
                        break;
                    default:
                        break;
                }
                EventBus.getDefault().post(new BatteryConnectEvent(true));
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                LogUtil.d("BatteryState", "ACTION_POWER_DISCONNECTED");
                EventBus.getDefault().post(new BatteryConnectEvent(false));
                break;
            case Intent.ACTION_POWER_USAGE_SUMMARY:
                LogUtil.d("BatteryState", "ACTION_POWER_USAGE_SUMMARY");

                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                LogUtil.d("ConnectivityManager", "receiver");

                break;
            default:
                break;
        }
    }

}
