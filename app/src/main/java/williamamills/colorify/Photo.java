package williamamills.colorify;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alexander on 3/23/2016.
 */
public class Photo implements Parcelable{
    private String caption;
    private String tags;
    //private String index;
    private String likes;
    private String bitmapAddress;
    private String location;

    public Photo(String _caption, String _tags, String _likes, String _bitmapAddress, String _location){
        caption = _caption;
        tags = _tags;
        likes = _likes;
        location = _location;
        bitmapAddress = _bitmapAddress;
    }
    public String getCaption(){return caption;}
    public String getTags(){return tags;}
    public String getLikes(){return likes;}
    public String getBitmapAddress(){return bitmapAddress;}
    public void setBitmapAddress(String _bitmapAddress){
        bitmapAddress = _bitmapAddress;
    }


    // Parcelling part
    public Photo(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);
        this.caption = data[0];
        this.tags = data[1];
        this.likes = data[2];
        this.bitmapAddress = data[3];
        this.location = data[4];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.caption,
                this.tags,
                this.likes, this.bitmapAddress, this.location});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}

