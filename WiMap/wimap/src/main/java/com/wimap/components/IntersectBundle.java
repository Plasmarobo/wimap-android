package com.wimap.components;

import android.os.Parcel;
import android.os.Parcelable;

import com.wimap.math.Intersect;
import com.wimap.math.RadialDistance;

import java.util.List;

public class IntersectBundle extends Intersect implements Parcelable {

	public IntersectBundle(List<RadialDistance> L, double x, double y, double z) {
		super(L, x, y, z);
	}
	public IntersectBundle(Intersect p)
	{
		super(p);
	}
	public IntersectBundle(Parcel in)
	{
			super();
			this.x = in.readDouble();
			this.y = in.readDouble();
			this.z = in.readDouble();
			this.x_conf = in.readDouble();
			this.y_conf = in.readDouble();
			this.z_conf = in.readDouble();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(z);
		out.writeDouble(x_conf);
		out.writeDouble(y_conf);
		out.writeDouble(z_conf);
	}
	public static final Parcelable.Creator<IntersectBundle> CREATOR = new Parcelable.Creator<IntersectBundle>() {
		public IntersectBundle createFromParcel(Parcel in) {
			return new IntersectBundle(in);
		}
		public IntersectBundle[] newArray(int size) {
			return new IntersectBundle[size];
		}
	};

}
