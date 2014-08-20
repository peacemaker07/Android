package com.example.ekimap4;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	// �}�b�v�I�u�W�F�N�g�i1�j
    private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
        // MapFragment�̎擾�i2�j
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        try {
            // �}�b�v�I�u�W�F�N�g���擾����i3�j
            googleMap = mapFragment.getMap();

            // Activity�����߂Đ������ꂽ�Ƃ��i4�j
            if (savedInstanceState == null) {

                // �t���O�����g��ۑ�����i5�j
                mapFragment.setRetainInstance(true);

                // �n�}�̏����ݒ���s���i6�j
                mapInit();
            }
        }
        // GoogleMap���g�p�ł��Ȃ��Ƃ�
        catch (Exception e) {
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	// �n�}�̏����ݒ�
	private void mapInit() {

	    // �n�}�^�C�v�ݒ�i1�j
	    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

	    // ���݈ʒu�{�^���̕\���i2�j
	    googleMap.setMyLocationEnabled(true);

	    // �����w�̈ʒu�A�Y�[���ݒ�i3�j
	    CameraPosition camerapos = new CameraPosition.Builder()
	            .target(new LatLng(35.681382, 139.766084)).zoom(15.5f).build();

	    // �n�}�̒��S��ύX����i4�j
	    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));
	}

}
