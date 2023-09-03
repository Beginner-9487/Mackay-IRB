package com.example.mackayirb.data.central;

import static com.example.mackayirb.utils.BasicResourceManager.SharedPreferencesManager.FootDeveloperMode;
import static com.example.mackayirb.utils.BasicResourceManager.SharedPreferencesManager.MackayClientMode;
import static com.example.mackayirb.utils.BasicResourceManager.SharedPreferencesManager.MackayDeveloperMode;

import com.example.mackayirb.R;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.OtherUsefulFunction;

public class FakeBytesGiver {

    public static int Index = 0;

    public static int getFrequency() {
        return 500;
    }

    public static int getNumberOfDataInLabel() {
        switch (BasicResourceManager.SharedPreferencesManager.getModeController()) {
            case MackayClientMode:
            case MackayDeveloperMode:
                return 70;
            case FootDeveloperMode:
                return 20;
        }
        return 0;
    }

    public static int getNumberOfTypes() {
        switch (BasicResourceManager.SharedPreferencesManager.getModeController()) {
            case MackayClientMode:
            case MackayDeveloperMode:
                return BasicResourceManager.getResources().getStringArray(R.array.TypeLabels).length;
            case FootDeveloperMode:
                return 2;
        }
        return 0;
    }

    public static byte currentType = getNewType();
    public static byte getNewType() {
        switch (BasicResourceManager.SharedPreferencesManager.getModeController()) {
            case MackayClientMode:
            case MackayDeveloperMode:
                return (byte) (Math.random() * getNumberOfTypes());
            case FootDeveloperMode:
                return (byte) (Math.random() * getNumberOfTypes() + 0x0a);
        }
        return 0;
    }

    public static byte[] getBytes() {
        int index = Index++;
        switch (BasicResourceManager.SharedPreferencesManager.getModeController()) {
            case MackayClientMode:
            case MackayDeveloperMode:
                return OtherUsefulFunction.concatWithArrayCopy(
                        new byte[]{
                                currentType,
                                0x00, (byte) (getNumberOfDataInLabel()), 0x00, (byte) (index+1), 0x00, 0x00, (byte) (index*100/256), (byte) (index*100%256), 0x00, 0x00,
                        },
                        OtherUsefulFunction.getRandomByteArray(4)
                );
            case FootDeveloperMode:
//                return OtherUsefulFunction.concatWithArrayCopy(
//                        new byte[]{
//                                getNewType(),
//                        },
//                        OtherUsefulFunction.getRandomByteArray(241)
//                );

//                return OtherUsefulFunction.concatWithArrayCopy(
//                    new byte[]{
//                            getNewType(),
//                    },
//                    OtherUsefulFunction.getRandomByteArray(24),
//                    OtherUsefulFunction.getSignedIntToByteArray(true, 60000,2, 31),
//                    OtherUsefulFunction.getSignedIntToByteArray(true, 30000,2, 31),
//                    OtherUsefulFunction.getSignedIntToByteArray(true, 35000,2, 31),
//                    OtherUsefulFunction.getSignedIntToByteArray(true, 20,1, 31)
//                );

                return OtherUsefulFunction.concatWithArrayCopy(
                        new byte[]{
                                getNewType(),
                        },
                        OtherUsefulFunction.getRandomByteArray(24),
                        OtherUsefulFunction.getSignedIntSequenceToByteArray(true, 0, 2000,2, 31),
                        OtherUsefulFunction.getSignedIntSequenceToByteArray(true, 0, 2000,2, 31),
                        OtherUsefulFunction.getSignedIntSequenceToByteArray(true, 33000, 1000,2, 31),
                        OtherUsefulFunction.getSignedIntSequenceToByteArray(true, 19, 1,1, 31)
                );
        }
        return OtherUsefulFunction.getRandomByteArray(1000);
    }
}
