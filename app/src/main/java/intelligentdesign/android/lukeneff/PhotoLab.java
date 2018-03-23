package intelligentdesign.android.lukeneff;

import android.content.Context;

import java.io.File;

/**
 * Created by brianlutz on 3/23/18.
 */

public class PhotoLab {
    private static final String PHOTO_FILE_NAME = "IMG_1.jpg";
    private static PhotoLab sPhotoLab;
    private Context mContext;

    public static PhotoLab get(Context context) {
        if (sPhotoLab == null) {
            sPhotoLab = new PhotoLab(context);
        }
        return sPhotoLab;
    }

    private PhotoLab(Context context) {
        mContext = context.getApplicationContext();
    }
    public File getPhotoFile() {
        File filesdir = mContext.getFilesDir();
        return new File(filesdir, PHOTO_FILE_NAME);
    }
}
