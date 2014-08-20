package com.facebook.android;
 
import java.util.Arrays;
import java.util.Date;
import java.util.List;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
 
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
 
public class MainActivity extends Activity {
 
	// Logのタグ
    private static final String TAG = "MainActivity";
    // Facebookのタイムライン投稿許可
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    // 編集テキスト
    private EditText mEditText;
    // Session状態が変更された場合にCallされる（Callbackを定義）
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            //updateView();
            Log.d(TAG, "SessionStatusCallback");
            onSessionStateChange(session, state, exception);
        }
    }
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
     
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// スーパークラスの処理実行 
        super.onCreate(savedInstanceState);
        // Activityに表示
        setContentView(R.layout.activity_main);
 
        // ログ出力
        Log.d(TAG,"onCreate");
         
        // 投稿用UI設定
        mEditText = (EditText) findViewById(R.id.message);
        Button btn = (Button) findViewById(R.id.post_button);
        
        // クリック処理を定義
        btn.setOnClickListener(new OnClickListener() {
             
            @Override
            public void onClick(View v) {
                //doPost();
                preparePublish();
            }
        });
         
        // Facebook ログイン管理
        // 現在のSessionを取得
        // memo:Sessionがない場合はnullを返却する
        Session session = Session.getActiveSession();
        if (session == null) {
        	// 初回起動か？？？
            // memo:savedInstanceStateは前回終了時の状態を保持している
            if (savedInstanceState != null) {
            	// 前回SessionをBundleから復元する
            	// memo:復元できなかった場合は、nullを返却する
            	//      onCreateメソッドからCallされていることを想定している
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
            	// Sessionが存在しない場合は新しくSessionを生成
                session = new Session(this);
            }
            // セッションをActiveにする
            Session.setActiveSession(session);
            // Sessionがまだ開かれずキャッシュされたTokenがない状態か？
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                //session.openForPublish(getOpenRequest());
            	// 認証を要求する
                session.openForRead(new OpenRequest(this));
            }
        }
 
        // ログイン状態の確認（Sessionが開かれているか）
        if (! session.isOpened()) {
            doLogin();
        }
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
        Log.d(TAG,"onStart");
    }
 
    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
        Log.d(TAG,"onStop");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        Log.d(TAG,"onResume:" + "session state is " + session.getState());
 
        if (session.getState().equals(SessionState.CLOSED_LOGIN_FAILED) || session.getState().equals(SessionState.CLOSED)) {
            Toast.makeText(this, "Facebook認証に失敗しました。", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult");
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
 
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onSaveInstanceState");
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
 
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if ((exception instanceof FacebookOperationCanceledException ||
                exception instanceof FacebookAuthorizationException)) {
            Log.w(TAG, "error occured:" + exception.getMessage());
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
            doPost();
        }
    }
     
    private void doLogin() {
    	// アクティブなSessionを取得
        Session session = Session.getActiveSession();
        Log.d(TAG,"doLogin: session state is " + session.getState() + ", isOpend:" + session.isOpened() + ", isClosed:" + session.isClosed());
        // 開いているSessionがない？
        if (!session.isOpened()) {
        	// Sessionが閉じているか？
            if (session.isClosed()) {
            	// Sessionを作成
                session = new Session(this);
                // SessionをActiveにする
                Session.setActiveSession(session);          
            }
            //session.openForPublish(getOpenRequest());
            // 認証を要求
            session.openForRead(new OpenRequest(this));
        } else {
        	// Sessionが開かれている場合
            Session.openActiveSession(this, true, statusCallback);
        }
    }
 
    private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }
 
    private void preparePublish() {
        Session session = Session.getActiveSession();
        if (session != null) {
            if (hasPublishPermission()) {
                // We can do the action right away.
                doPost();
            } else {
                // We need to get new permissions, then complete the action when we get called back.
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSIONS));
            }
        }
    }
     
    private void doPost() {
        Log.d(TAG,"doPost");
        final String message = "投稿テストです:" + (new Date().toString()) + "\n" + mEditText.getText().toString();
        Request request = Request
                .newStatusUpdateRequest(Session.getActiveSession(), message, new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        showPublishResult(message, response.getGraphObject(), response.getError());
                    }
                });
        request.executeAsync();     
    }
 
    private interface GraphObjectWithId extends GraphObject {
        String getId();
    }
    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
        Log.d(TAG,"showPublishResult");
        String title = null;
        String alertMessage = null;
        if (error == null) {
            title = "ステータスアップデート成功";
            String id = result.cast(GraphObjectWithId.class).getId();
            alertMessage = "ID=" + id;
        } else {
            title = "ステータスアップデート失敗";
            alertMessage = error.getErrorMessage();
        }
 
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton("OK", null)
                .show();
    }
     
}