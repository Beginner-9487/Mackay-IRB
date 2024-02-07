package com.example.mackayirb.data.central;

import com.example.mackayirb.utils.BasicResourceManager;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Arrays;

public class FootSensorPositions {
    public static ArrayList<Entry> getAllEntry() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.addAll(Foot_Left);
        entries.addAll(Foot_Right);
        return entries;
    }

    public static ArrayList<Entry> getPositions(byte position) {
        switch (position) {
            case FootLabelData.Position.LEFT_FOOT:
                return Foot_Left;
            case FootLabelData.Position.RIGHT_FOOT:
                return Foot_Right;
        }
        return Foot_Left;
    }

    public static ArrayList<Entry> Foot_Left = new ArrayList<>(Arrays.asList(
            new Entry(114f * BasicResourceManager.getResources().getDisplayMetrics().density, 70f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(162f * BasicResourceManager.getResources().getDisplayMetrics().density, 72f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(77f * BasicResourceManager.getResources().getDisplayMetrics().density, 104f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(131f * BasicResourceManager.getResources().getDisplayMetrics().density, 108f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(185f * BasicResourceManager.getResources().getDisplayMetrics().density, 113f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(65f * BasicResourceManager.getResources().getDisplayMetrics().density, 150f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(125f * BasicResourceManager.getResources().getDisplayMetrics().density, 153f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(184f * BasicResourceManager.getResources().getDisplayMetrics().density, 158f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(45f * BasicResourceManager.getResources().getDisplayMetrics().density, 196f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(90f * BasicResourceManager.getResources().getDisplayMetrics().density, 199f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(139f * BasicResourceManager.getResources().getDisplayMetrics().density, 201f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(184f * BasicResourceManager.getResources().getDisplayMetrics().density, 204f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(32f * BasicResourceManager.getResources().getDisplayMetrics().density, 247f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(80f * BasicResourceManager.getResources().getDisplayMetrics().density, 249f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(133f * BasicResourceManager.getResources().getDisplayMetrics().density, 251f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(47f * BasicResourceManager.getResources().getDisplayMetrics().density, 297f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(177f * BasicResourceManager.getResources().getDisplayMetrics().density, 253f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(102f * BasicResourceManager.getResources().getDisplayMetrics().density, 299f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(158f * BasicResourceManager.getResources().getDisplayMetrics().density, 300f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(48f * BasicResourceManager.getResources().getDisplayMetrics().density, 351f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(140f * BasicResourceManager.getResources().getDisplayMetrics().density, 351f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(65f * BasicResourceManager.getResources().getDisplayMetrics().density, 392f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(111f * BasicResourceManager.getResources().getDisplayMetrics().density, 391f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(64f * BasicResourceManager.getResources().getDisplayMetrics().density, 465f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(110f * BasicResourceManager.getResources().getDisplayMetrics().density, 464f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(63f * BasicResourceManager.getResources().getDisplayMetrics().density, 514f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(109f * BasicResourceManager.getResources().getDisplayMetrics().density, 512f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(61f * BasicResourceManager.getResources().getDisplayMetrics().density, 569f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(108f * BasicResourceManager.getResources().getDisplayMetrics().density, 567f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(60f * BasicResourceManager.getResources().getDisplayMetrics().density, 624f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(107f * BasicResourceManager.getResources().getDisplayMetrics().density, 622f * BasicResourceManager.getResources().getDisplayMetrics().density)
    ));
    public static ArrayList<Entry> Foot_Right = new ArrayList<>(Arrays.asList(
            new Entry(339f * BasicResourceManager.getResources().getDisplayMetrics().density, 70f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(291f * BasicResourceManager.getResources().getDisplayMetrics().density, 72f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(376f * BasicResourceManager.getResources().getDisplayMetrics().density, 104f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(322f * BasicResourceManager.getResources().getDisplayMetrics().density, 108f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(268f * BasicResourceManager.getResources().getDisplayMetrics().density, 113f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(388f * BasicResourceManager.getResources().getDisplayMetrics().density, 150f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(328f * BasicResourceManager.getResources().getDisplayMetrics().density, 153f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(269f * BasicResourceManager.getResources().getDisplayMetrics().density, 158f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(408f * BasicResourceManager.getResources().getDisplayMetrics().density, 196f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(363f * BasicResourceManager.getResources().getDisplayMetrics().density, 199f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(314f * BasicResourceManager.getResources().getDisplayMetrics().density, 201f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(269f * BasicResourceManager.getResources().getDisplayMetrics().density, 204f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(421f * BasicResourceManager.getResources().getDisplayMetrics().density, 247f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(373f * BasicResourceManager.getResources().getDisplayMetrics().density, 249f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(320f * BasicResourceManager.getResources().getDisplayMetrics().density, 251f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(276f * BasicResourceManager.getResources().getDisplayMetrics().density, 253f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(406f * BasicResourceManager.getResources().getDisplayMetrics().density, 297f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(351f * BasicResourceManager.getResources().getDisplayMetrics().density, 299f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(295f * BasicResourceManager.getResources().getDisplayMetrics().density, 300f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(405f * BasicResourceManager.getResources().getDisplayMetrics().density, 351f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(313f * BasicResourceManager.getResources().getDisplayMetrics().density, 351f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(388f * BasicResourceManager.getResources().getDisplayMetrics().density, 392f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(342f * BasicResourceManager.getResources().getDisplayMetrics().density, 391f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(389f * BasicResourceManager.getResources().getDisplayMetrics().density, 465f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(343f * BasicResourceManager.getResources().getDisplayMetrics().density, 464f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(390f * BasicResourceManager.getResources().getDisplayMetrics().density, 514f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(344f * BasicResourceManager.getResources().getDisplayMetrics().density, 512f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(392f * BasicResourceManager.getResources().getDisplayMetrics().density, 569f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(345f * BasicResourceManager.getResources().getDisplayMetrics().density, 567f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(393f * BasicResourceManager.getResources().getDisplayMetrics().density, 624f * BasicResourceManager.getResources().getDisplayMetrics().density),
            new Entry(346f * BasicResourceManager.getResources().getDisplayMetrics().density, 622f * BasicResourceManager.getResources().getDisplayMetrics().density)
    ));
}
