package com.gkzxhn.prison.view;

import com.gkzxhn.prison.entity.VersionEntity;

/**
 * Created by Raleigh.Luo on 18/3/28.
 */

public interface ISettingView extends IBaseView{
    void updateVersion(VersionEntity version);
    void updateFreeTime(int time);
}
