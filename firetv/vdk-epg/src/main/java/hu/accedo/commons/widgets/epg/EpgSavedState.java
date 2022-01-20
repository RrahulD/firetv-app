/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.widgets.epg;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View.BaseSavedState;

public class EpgSavedState extends BaseSavedState {
    public int X;
    public int Y;

    EpgSavedState(Parcelable superState) {
        super(superState);
    }

    private EpgSavedState(Parcel in) {
        super(in);
        X = in.readInt();
        Y = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(X);
        out.writeInt(Y);
    }

    public static final Parcelable.Creator<EpgSavedState> CREATOR = new Parcelable.Creator<EpgSavedState>() {
        public EpgSavedState createFromParcel(Parcel in) {
            return new EpgSavedState(in);
        }

        public EpgSavedState[] newArray(int size) {
            return new EpgSavedState[size];
        }
    };
}
