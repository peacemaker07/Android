package sample.android.camera01;

import java.io.IOException;
import java.util.List;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.Camera.PictureCallback;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.provider.MediaStore;
import android.view.SurfaceView;

public class SampleView extends SurfaceView implements Callback, PictureCallback {
	private Camera camera;
	private Size   cSize;

	public SampleView(Context context) {
		super(context);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();
			camera.setDisplayOrientation(90);
			camera.setPreviewDisplay(holder);
		} catch(IOException e) {
		}
		Camera.Parameters param = camera.getParameters();
		List<Size> supportedSizes = param.getSupportedPreviewSizes();
		cSize = supportedSizes.get(0);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
        camera.stopPreview();
        Camera.Parameters param = camera.getParameters();
//        param.setRotation(90);
//        param.set("orientation", "portrait");
//		camera.setDisplayOrientation(90);
        param.setPreviewSize(cSize.width, cSize.height);
        camera.setParameters(param);
        camera.startPreview();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
	}
	@Override
	public void onPictureTaken(byte[] data, Camera c) {
//TODO:Activity‚©‚ç‰æ‘œ‚ÌŒü‚«‚ğæ“¾‚µ‚Äbitmap‚ğ’²®‚·‚é
//		int degrees = getCameraDisplayOrientation(null); // ‰æ‘œ‚ÌŒü‚«‚ğæ“¾
//	     Matrix m = new Matrix();
//	     m.postRotate(degrees);
	     Matrix m = new Matrix();
	     m.postRotate(90);
	     Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, null);
	     bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
		MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bmp, "", null);
		camera.startPreview();
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if(me.getAction()==MotionEvent.ACTION_DOWN) {
			camera.takePicture(null,null,this);
		}
		return true;
	}
	
	public static int getCameraDisplayOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
        	case Surface.ROTATION_0:	degrees = 0;	break;
        	case Surface.ROTATION_90:   degrees = 90;   break;
        	case Surface.ROTATION_180:  degrees = 180;  break;
        	case Surface.ROTATION_270:  degrees = 270;  break;
        }
        return (90 + 360 - degrees) % 360;
    }
}